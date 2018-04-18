package dunegon.game;

import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {
    private Logger LOGGER = LoggerFactory.getLogger(Map.class.getSimpleName());
    private static final int BLOCK_SIZE = 32;
    // z y x
    private java.util.Map<Integer, TileBlock>[] tileBlocks;
    private java.util.Map<Long, Creature> knownCreatures;

    private AwareRange awareRange;
    private Position centralPosition;

    public Map() {
        knownCreatures = new HashMap<>();
        tileBlocks = new HashMap[Consts.MAX_Z + 1];
        for (int i = 0; i <= Consts.MAX_Z; i++) {
            tileBlocks[i] = new HashMap<>();
        }

        init();
    }

    public void init() {
        resetAwareRange();
    }

    public void setAwareRange(AwareRange awareRange) {
        this.awareRange = awareRange;
        // todo: remove unaware things
    }

    public void resetAwareRange() {
        AwareRange awareRange = new AwareRange(6, 9, 7, 8);
        setAwareRange(awareRange);
    }

    public AwareRange getAwareRange() {
        return awareRange;
    }

    public Position getCentralPosition() {
        return centralPosition;
    }

    public void setCentralPosition(Position centralPosition) {
        if (this.centralPosition == centralPosition) {
            return;
        }

        this.centralPosition = centralPosition;

        removeUnawareThings();
    }

    public void addThing(Thing thing, Position position) {
        addThing(thing, position, -1);
    }

    public void addThing(Thing thing, Position position, int stackPos) {
        LOGGER.info("addThing, id: {}, postion: {}", thing.getId(), position);
        // todo
        if (thing == null) {
            return;
        }

        if (thing.isItem() || thing.isCreature() || thing.isEffect()) {
            Tile tile = getOrCreateTile(position);
            if (tile != null) {
                tile.addThing(thing, stackPos);
            } else {
                LOGGER.error("no tile!");
            }
        } // todo : other things
    }

    public Tile getOrCreateTile(Position position) {
        if (!position.isMapPosition()) {
            return null; // todo: nulltile
        }

        TileBlock block = getTileBlock(position);
        return block.getOrCreate(position);
    }

    public Tile getTile(Position position) {
        if (!position.isMapPosition()) {
            return null;
        }

        TileBlock tileBlock = tileBlocks[position.getZ()].get(getBlockIndex(position));
        if (tileBlock != null) {
            return tileBlock.get(position);
        }

        return null;
    }

    public void cleanTile(Position position) {
        if (!position.isMapPosition()) {
            return;
        }
        TileBlock tileBlock = tileBlocks[position.getZ()].get(getBlockIndex(position));
        if (tileBlock != null) {
            Tile tile = tileBlock.get(position);
            if (tile != null) {
                tile.clean();
                if (tile.canErase()) {
                    tileBlock.remove(position);
                }
            }
        }
    }

    public Thing getThing(Position position, int stackPos) {
        Tile tile = getTile(position);
        if (tile != null) {
            return tile.getThing(stackPos);
        }
        return null;
    }

    public boolean removeThing(Thing thing) {
        if (thing == null) {
            return false;
        }
        boolean removed = false;
        // todo implement missile etc
        Tile tile = thing.getTile();
        if (tile != null) {
            removed = tile.removeThing(thing);
        }
        return removed;
    }

    public void addCreature(Creature creature) {
        knownCreatures.put(creature.getId(), creature);
    }

    public Creature getCreatureById(long id) {
        return knownCreatures.get(id);
    }

    public void removeCreatureById(long id) {
        knownCreatures.remove(id);
    }

    public void removeUnawareThings() {
        for (Long key : knownCreatures.keySet()) {
            Creature creature = knownCreatures.get(key);
            if (!isAwareOfPosition(creature.getPosition())) {
                removeThing(creature);
            }
        }

        // todo: remove static texts

        for (int z = 0; z <= Consts.MAX_Z; z++) {
            java.util.Map<Integer, TileBlock> tileBlockMap = tileBlocks[z];
            List<Integer> blocksToRemove = new ArrayList();
            for (Integer key : tileBlockMap.keySet()) {
                TileBlock tileBlock = tileBlockMap.get(key);
                boolean blockEmpty = true;
                for (Tile tile : tileBlock.getTiles()) {
                    if (tile == null) {
                        continue;
                    }

                    Position position = tile.getPosition();
                    if (!isAwareOfPosition(position)) {
                        tileBlock.remove(position);
                    } else {
                        blockEmpty = false;
                    }
                }
                if (blockEmpty) {
                    blocksToRemove.add(key);
                }
            }
            for (Integer key : blocksToRemove) {
                tileBlockMap.remove(key);
            }
        }
    }

    public int getFirstAwareFloor() {
        if (centralPosition.getZ() > Consts.SEA_FLOOR) {
            return centralPosition.getZ() - Consts.AWARE_UNDEGROUND_FLOOR_RANGE;
        } else {
            return 0;
        }
    }

    public int getLastAwareFloor() {
        if (centralPosition.getZ() > Consts.SEA_FLOOR) {
            return Math.min(centralPosition.getZ() + Consts.AWARE_UNDEGROUND_FLOOR_RANGE, Consts.MAX_Z);
        } else {
            return Consts.SEA_FLOOR;
        }
    }

    public boolean isAwareOfPosition(Position position) {
        if (position.getZ() < getFirstAwareFloor() || position.getZ() > getLastAwareFloor()) {
            return false;
        }

        Position groundedPosition = position;
        while (groundedPosition.getZ() != centralPosition.getZ()) {
            if (groundedPosition.getZ() > centralPosition.getZ()) {
                // When pos == 65535,65535,15 we cant go up to 65536,65536,14
                if (groundedPosition.getX() == 65535 || groundedPosition.getY() == 65535) {
                    break;
                }
                groundedPosition = groundedPosition.coveredUp();
            } else {
                // When pos == 0,0,0 we cant go down to -1,-1,1
                if (groundedPosition.getX() == 0 || groundedPosition.getY() == 0) {
                    break;
                }
                groundedPosition = groundedPosition.coveredDown();
            }
        }

        return centralPosition.isInRange(groundedPosition, awareRange.getLeft(),
                awareRange.getRight(), awareRange.getTop(), awareRange.getBottom());
    }

    // private
    private int getBlockIndex(Position position) {
        return ((position.getY() / BLOCK_SIZE) * (65536 / BLOCK_SIZE)) + (position.getX() / BLOCK_SIZE);
    }

    private TileBlock getTileBlock(Position position) {
        int index = getBlockIndex(position);
        int z = position.getZ();
        if (tileBlocks[z].get(index) == null) {
            tileBlocks[z].put(index, new TileBlock());
        }
        return tileBlocks[z].get(index);
    }

    class TileBlock {
        private Tile[] tiles;

        public TileBlock() {
            tiles = new Tile[Map.BLOCK_SIZE * Map.BLOCK_SIZE];
        }

        public Tile create(Position position) {
            int index = getTileIndex(position);
            tiles[index] = new Tile(position);
            return tiles[index];

        }

        public Tile getOrCreate(Position position) {
            int index = getTileIndex(position);
            if (tiles[index] == null) {
                tiles[index] = new Tile(position);
            }
            return tiles[index];
        }

        public Tile get(Position position) {
            int index = getTileIndex(position);
            return tiles[index];
        }

        public Tile[] getTiles() {
            return tiles;
        }

        public void remove(Position position) {
            int index = getTileIndex(position);
            tiles[index] = null;
        }

        int getTileIndex(Position position) {
            return ((position.getY() % BLOCK_SIZE) * BLOCK_SIZE) + (position.getX() % BLOCK_SIZE);
        }
    }
}
