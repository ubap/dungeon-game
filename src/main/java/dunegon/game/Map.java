package dunegon.game;

public class Map {

    private Tile[][][] tiles;

    public Map() {
        this.tiles = new Tile[15][Consts.MAP_WIDTH][Consts.MAP_HEIGHT];
    }

    public void addThing(Thing thing, Position position, int stackPos) {
        // todo
    }

    public void cleanTile(Position position) {
        tiles[position.getZ()][position.getY()][position.getX()].clean();
    }
}
