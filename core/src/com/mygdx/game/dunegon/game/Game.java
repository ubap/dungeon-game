package com.mygdx.game.dunegon.game;

public class Game {
    private static Game INSTANCE;

    private Map map;

    private Game() {
        this.map = new Map();
    }

    public static Game getInstance() {
        return INSTANCE;
    }

    /**
     * Has to be called before getInstance;
     */
    public static void init() {
        INSTANCE = new Game();
    }

    public Map getMap() {
        return this.map;
    }
}
