package com.mygdx.game.dunegon.game;

import com.mygdx.game.dunegon.net.ProtocolGame;

public class Game {
    private static Game INSTANCE;

    private ProtocolGame protocolGame;
    private Map map;

    private int serverBeat;

    private Game() {
        this.map = new Map();
    }

    public static Game getInstance() {
        return INSTANCE;
    }

    /**
     * Has to be called before getInstance;
     */
    public static void init(ProtocolGame protocolGame) {
        INSTANCE = new Game();
        INSTANCE.protocolGame = protocolGame;
    }

    public Map getMap() {
        return this.map;
    }

    public int getServerBeat() {
        return serverBeat;
    }
    public void setServerBeat(int serverBeat) {
        this.serverBeat = serverBeat;
    }

    public void forceWalk(Consts.Direction direction) {
        switch (direction) {
            case WEST:
                protocolGame.sendWalkWest();
                break;
            case NORTH:
                protocolGame.sendWalkNorth();
                break;
            case EAST:
                protocolGame.sendWalkEast();
                break;
            case SOUTH:
                protocolGame.sendWalkSouth();
                break;
            default:
                break;
        }
    }
}
