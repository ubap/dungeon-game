package com.mygdx.game.dunegon.game;

public class Consts {

    public static final int MAP_HEIGHT = 14;
    public static final int MAP_WIDTH = 18;

    public static final int SEA_FLOOR = 7;
    public static final int MAX_Z = 15;
    public static final int AWARE_UNDEGROUND_FLOOR_RANGE = 2;

    public static class Skill {
        public static final int FIST = 0;
        public static final int CLUB = 1;
        public static final int SWORD = 2;
        public static final int AXE = 3;
        public static final int DISTANCE = 4;
        public static final int SHIELDING = 5;
        public static final int FISHING = 6;
        public static final int CRITICALCHANCE = 7;
        public static final int CRITICALDAMAGE = 8;
        public static final int LIFELEECHCHANCE = 9;
        public static final int LIFELEECHAMOUT = 10;
        public static final int MANALEECHCHANCE = 11;
        public static final int MANAlEECHMOUNT = 12;
        public static final int LASTSKILL = 13;
    }

    public static class Message {
        public static final int NONE = 0;
        public static final int SAY = 1;
        public static final int WHISPER = 2;
        public static final int YELL = 3;
        public static final int PRIVATE_FROM = 4;
        public static final int PRIVATE_TO = 5;
        public static final int CHANNEL_MANAGEMENT = 6;
        public static final int CHANNEL = 7;
        public static final int CHANNEL_HIGHLIGHT = 8;
        public static final int SPELL = 9;
        public static final int NPC_FROM_START_BLOCK = 10;
        public static final int NPC_FROM = 11;
        public static final int NPC_TO = 12;
        public static final int GAMEMASTER_BROADCAST = 13;
        public static final int GAMEMASTER_CHANNEL = 14;
        public static final int GAMEMASTER_PRIVATE_FROM = 15;
        public static final int GAMEMASTER_PRIVATE_TO = 16;
        public static final int LOGIN = 17;
        public static final int WARNING = 18;
        public static final int GAME = 19;
        public static final int GAME_HIGHLIGHT = 20;
        public static final int FAILURE = 21;
        public static final int LOOK = 22;
        public static final int DAMAGE_DEALED = 23;
        public static final int DAMAGE_RECEIVED = 24;
        public static final int HEAL = 25;
        public static final int EXP = 26;
        public static final int DAMAGE_OTHERS = 27;
        public static final int HEAL_OTHERS = 28;
        public static final int EXP_OTHERS = 29;
        public static final int STATUS = 30;
        public static final int LOOT = 31;
        public static final int TRADE_NPC = 32;
        public static final int GUILD = 33;
        public static final int PARTY_MANAGEMENT = 34;
        public static final int PARTY = 35;
        public static final int BARK_LOW = 36;
        public static final int BARK_LOUD = 37;
        public static final int REPORT = 38;
        public static final int HOTKEY_USE = 39;
        public static final int TUTORIAL_HINT = 40;
        public static final int THANK_YOU = 41;
        public static final int MARKET = 42;
        public static final int MANA = 43;
        public static final int BEYOND_LAST = 42;
        public static final int INVALID = 255;
    }

    public enum Direction {
        NORTH, EAST, SOUTH, WEST, NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST, INVALID_DIRECTION;

        public static Direction fromInt(int val) {
            switch (val) {
                case 0:
                    return NORTH;
                case 1:
                    return EAST;
                case 2:
                    return SOUTH;
                case 3:
                    return WEST;
                case 4:
                    return NORTH_EAST;
                case 5:
                    return SOUTH_EAST;
                case 6:
                    return SOUTH_WEST;
                case 7:
                    return NORTH_WEST;
                default:
                    return INVALID_DIRECTION;
            }
        }
    }

}
