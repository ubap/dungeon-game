package dunegon.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

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
        for (int i = 0; i < Consts.MAX_Z; i++) {
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
        this.centralPosition = centralPosition;
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
