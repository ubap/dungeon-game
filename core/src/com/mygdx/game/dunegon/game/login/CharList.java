package com.mygdx.game.dunegon.game.login;

import java.util.ArrayList;
import java.util.List;

public class CharList {
    private List<World> worlds;
    private List<Character> characters;
    private boolean premium;
    private long premDays;

    public CharList() {
        reset();
    }

    public void reset() {
        worlds = new ArrayList<World>();
        characters = new ArrayList<Character>();
        premium = false;
        premDays = 0;
    }

    public void setWorlds(List<World> worlds) {
        this.worlds = worlds;
    }

    public void setCharacterList(List<Character> characters) {
        this.characters = characters;
    }

    public void setPremium(boolean isPremium) {
        this.premium = isPremium;
    }

    public void setPremDays(long premDays) {
        this.premDays = premDays;
    }
}
