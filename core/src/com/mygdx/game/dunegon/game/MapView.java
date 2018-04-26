package com.mygdx.game.dunegon.game;

import com.mygdx.game.graphics.Point;
import com.mygdx.game.graphics.Size;

import java.util.ArrayList;
import java.util.List;

public class MapView {
    private static MapView INSTANCE;

    private int cachedFirstVisibleFloor;
    private int cachedLastVisibleFloor;
    private Size visibleDimension;
    private Size drawDimension;
    private int tileSize;
    private Point virtualCenterOffset;

    private int updateTilesPos;
    private List<Tile> cachedVisibleTiles;
    private boolean mustDrawVisibleTilesCache;
    private boolean mustUpdateTilesCache;

    private MapView() {
        this.cachedVisibleTiles = new ArrayList<Tile>();
        this.updateTilesPos = 0;
        this.mustUpdateTilesCache = true;
        setVisibleDimension(new Size(15, 11));
    }

    public static void init() {
        INSTANCE = new MapView();
    }

    public static MapView getInstance() {
        return INSTANCE;
    }

    public void onTileUpdate(Position position) {
        this.mustUpdateTilesCache = true;
    }

    public void draw(float scaleFactor) {
        // temporary solution
        this.tileSize = (int)(32f * scaleFactor);

        if (mustUpdateTilesCache) {
            updateVisibleTilesCache(this.updateTilesPos);
        }

        mustDrawVisibleTilesCache = true;
        if (mustDrawVisibleTilesCache) {

            int i = 0;
            for (int z = cachedLastVisibleFloor; z>= cachedFirstVisibleFloor; z--) {
                while (i < cachedVisibleTiles.size()) {
                    Tile tile = cachedVisibleTiles.get(i);
                    Position tilePos = tile.getPosition();
                    if (tilePos.getZ() != z) {
                        break;
                    } else {
                        i++;
                    }

                    tile.draw(transformPositionTo2D(tilePos, getCameraPosition()), scaleFactor, 0xFF);
                }
            }

            mustDrawVisibleTilesCache = false;
        }
    }

    private void updateVisibleTilesCache(int start) {
        if (start == 0) {
            this.cachedFirstVisibleFloor = calcFirstVisibleFloor();
            this.cachedLastVisibleFloor = calcLastVisibleFloor();

            if (this.cachedFirstVisibleFloor < 0 || this.cachedLastVisibleFloor < 0
                    || cachedFirstVisibleFloor > Consts.MAX_Z || cachedLastVisibleFloor > Consts.MAX_Z) {
                throw new RuntimeException("assert");
            }

            if (cachedLastVisibleFloor < cachedFirstVisibleFloor) {
                cachedLastVisibleFloor = cachedFirstVisibleFloor;
            }
        }

        Position cameraPosition = getCameraPosition();

        boolean stop = false;

        // clear current visible tiles cache
        this.cachedVisibleTiles.clear();
        this.mustDrawVisibleTilesCache = true;
        this.mustUpdateTilesCache = false;
        this.updateTilesPos = 0;

        // cache visible tiles in draw order
        // draw from last floor (the lower) to first floor (the higher)
        for(int iz = this.cachedLastVisibleFloor; iz >= cachedFirstVisibleFloor && !stop; --iz) {
            int numDiagonals = this.drawDimension.getWidth() + drawDimension.getHeight() - 1;
            // loop through / diagonals beginning at top left and going to top right
            for (int diagonal = 0; diagonal < numDiagonals && !stop; ++diagonal) {
                // loop through current tiles
                int advance = Math.max(diagonal - this.drawDimension.getHeight(), 0);
                for (int iy = diagonal - advance, ix = advance; iy >= 0 && ix < drawDimension.getWidth() && !stop; iy--, ix++) {
                    if (this.updateTilesPos < start) {
                        this.updateTilesPos++;
                        continue;
                    }

                    // position on current floor
                    Position tilePos = cameraPosition.translated(ix - virtualCenterOffset.getX(), iy - virtualCenterOffset.getY());
                    // adjust tilePos to the wanted floor
                    tilePos = tilePos.coveredUp(cameraPosition.getZ() - iz);
                    Tile tile = Game.getInstance().getMap().getTile(tilePos);
                    if (tile != null) {
                        if (!tile.isDrawable()) {
                            continue;
                        }
                        if (Game.getInstance().getMap().isCompletelyCovered(tilePos, cachedFirstVisibleFloor)) {
                            continue;
                        }

                        this.cachedVisibleTiles.add(tile);
                    }
                    this.updateTilesPos++;
                }
            }
        }

        if (!stop) {
            this.updateTilesPos = 0;
        }

    }

    private int calcFirstVisibleFloor() {
        int z = 7;
        Position cameraPosition = getCameraPosition();
        if (cameraPosition == null) {
            return z;
        }

        int firstFloor = 0;
        // limits to underground floors while under sea level
        if (cameraPosition.getZ() > Consts.SEA_FLOOR) {
            firstFloor = Math.max(cameraPosition.getZ() - Consts.AWARE_UNDEGROUND_FLOOR_RANGE, Consts.UNDERGROUND_FLOOR);
        }

        // loop in 3x3 tiles around the camera
        for (int ix = -1; ix <= 1 && firstFloor < cameraPosition.getZ(); ix++) {
            for (int iy = -1; iy <= 1 && firstFloor < cameraPosition.getZ(); iy++) {
                Position position = cameraPosition.translated(ix, iy);

                // process tiles that we can look through, e.g. windows, doors
                if ( (ix == 0 && iy == 0) || (Math.abs(ix) != Math.abs(iy) && Game.getInstance().getMap().isLookPossible(position)) ) {
                    Position upperPos = position;
                    Position coveredPos = position;

                    while (true) {
                        coveredPos = coveredPos.coveredUp();
                        if (coveredPos == null) {
                            break;
                        }
                        upperPos = upperPos.up();
                        if (upperPos == null) {
                            break;
                        }
                        if (upperPos.getZ() < firstFloor) {
                            break;
                        }
                        // check tiles physically above
                        Tile tile = Game.getInstance().getMap().getTile(upperPos);
                        if (tile != null && tile.limitsFloorView(!Game.getInstance().getMap().isLookPossible(position))) {
                            firstFloor = upperPos.getZ() + 1;
                            break;
                        }

                        tile = Game.getInstance().getMap().getTile(coveredPos);
                        if (tile != null && tile.limitsFloorView(Game.getInstance().getMap().isLookPossible(position))) {
                            firstFloor = coveredPos.getZ() + 1;
                            break;
                        }

                    }
                }
            }
        }

        z = Math.max(firstFloor, 0);
        z = Math.min(z, Consts.MAX_Z);
        return z;
    }

    private int calcLastVisibleFloor() {
        int z = 7;

        Position cameraPosition = getCameraPosition();

        if (cameraPosition != null) {
            if (cameraPosition.getZ() > Consts.SEA_FLOOR) {
                z = cameraPosition.getZ() + Consts.AWARE_UNDEGROUND_FLOOR_RANGE;
            } else {
                z = Consts.SEA_FLOOR;
            }
        }

        z = Math.max(z, 0);
        z = Math.min(z, Consts.MAX_Z);
        return z;
    }

    private Position getCameraPosition() {
        return Game.getInstance().getMap().getCentralPosition();
    }

    public void setVisibleDimension(Size visibleDimension) {
        if (this.visibleDimension == visibleDimension) {
            return;
        }

        if (visibleDimension.getWidth() % 2 != 1 || visibleDimension.getHeight() % 2 != 1) {
            throw new RuntimeException("visible dimension must be odd");
        }

        this.visibleDimension = visibleDimension;
        this.drawDimension = visibleDimension.add(new Size(3,3));
        this.virtualCenterOffset = drawDimension.multiply(0.5f).add(new Size(-1, -1)).toPoint();
        this.tileSize = 32;
    }

    private Point transformPositionTo2D(Position position, Position relativePosition) {
        return new Point((virtualCenterOffset.getX() + (position.getX() - relativePosition.getX()) - (relativePosition.getZ() - position.getZ())) * tileSize,
                (virtualCenterOffset.getY() + (position.getY() - relativePosition.getY()) - (relativePosition.getZ() - position.getZ())) * tileSize);
    }

}
