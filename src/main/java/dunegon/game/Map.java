package dunegon.game;

import javafx.geometry.Pos;

import java.util.HashMap;

public class Map {
    private static final int BLOCK_SIZE = 32;
    // z y x
    private java.util.Map<Integer, TileBlock>[] tileBlocks;
    private java.util.Map<Long, Creature> knownCreatures;

    public Map() {
        tileBlocks = new HashMap[Consts.MAX_Z + 1];
        for (int i = 0; i < Consts.MAX_Z; i++) {
            tileBlocks[i] = new HashMap<>();
        }
    }

    public void addThing(Thing thing, Position position) {
        addThing(thing, position, -1);
    }

    public void addThing(Thing thing, Position position, int stackPos) {
        // todo
        if (thing == null) {
            return;
        }

        if (thing.isItem() || thing.isCreature() || thing.isEffect()) {
            Tile tile = getOrCreateTile(position);
            if (tile != null) {
                tile.addThing(thing, stackPos);
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

    public void addCreature(Creature creature) {
        knownCreatures.put(creature.getId(), creature);
    }

    public Creature getCreatureById(long id) {
        return knownCreatures.get(id);
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

        public void remove(Position position) {
            int index = getTileIndex(position);
            tiles[index] = null;
        }

        int getTileIndex(Position position) {
            return ((position.getY() % BLOCK_SIZE) * BLOCK_SIZE) + (position.getX() % BLOCK_SIZE);
        }
    }
}
