package com.mygdx.game.dunegon.net;

public class Proto {
    private Proto() { }

    public static final char CLIENT_PENDING_GAME = 0x1;

    public static class Login {
        public static final byte DISCONNECT = 0xB;
        public static final byte MOTD = 0x14;
        public static final byte SESSION_KEY = 0x28;
        public static final byte CHAR_LIST = 0x64;
    }

    public static class OpCode {
        // gameworld
        public static final byte DEATH = 0x28;
        public static final byte GAMEWORLD_PING = 0x1D;
        public static final byte GAMEWORLD_PING_BACK = 0x1E;
        public static final byte GAMEWORLD_FIRST_PACKET = 0x1F;
        public static final byte GAMESERVER_LOGIN_SUCCESS = 0x17;

        public static final short CREATURE_SAY = 0xAA;

        public static final byte PENDING_STATE = 0xA;
        public static final byte ENTER_WORLD = 0x0F;



        public static final short MAP_DESCRIPTION = 0x64;
        public static final short MAP_TOP_ROW = 0x65;
        public static final short MAP_RIGHT_ROW = 0x66;
        public static final short MAP_BOTTOM_ROW = 0x67;
        public static final short MAP_LEFT_ROW = 0x68;
        public static final short CREATE_ON_MAP = 0x6A;
        public static final short CHANGE_ON_MAP = 0x6B;
        public static final short DELETE_ON_MAP = 0x6C;
        public static final short MOVE_CREATURE = 0x6D;

        public static final short SET_INVENTORY = 0x78;
        public static final short DELETE_INVENTORY = 0x79;
        public static final short AMBIENT_LIGHT = 0x82;
        public static final short GRAPHICAL_EFFECT = 0x83;
        public static final short MISSILE_EFFECT = 0x85;
        public static final short CREATURE_LIGHT = 0x8D;
        public static final short CREATURE_HEALTH = 0x8C;
        public static final short CREATURE_SPEED = 0x8F;
        public static final short CREATURE_SKULL = 0x90;
        public static final short CREATURE_MARKS = 0x93;
        public static final short CREATURE_TYPE = 0x95;

        public static final short PLAYER_BASIC_DATA = 0x9F;
        public static final short PLAYER_STATS = 0xA0;
        public static final short PLAYER_SKILLS = 0xA1;
        public static final short PLAYER_STATE = 0xA2;
        public static final short TEXT_MESSAGE = 0xB4;
        public static final short PLAYER_INVENTORY = 0xF5;


        public static class Send {
            public static final byte TURN_NORTH = 0x6F;
            public static final byte TURN_EAST = 0x70;
        }
    }

    public static class ItemOpCode {
        public static final short UNKNOWN_CREATURE = 97;
        public static final short OUTDATED_CREATUER = 98;
        public static final short CREATURE = 99;

    }

    public static class CreatureType {
        public static final short PLAYER = 0;
        public static final short MONSTER = 1;
        public static final short NPC = 2;
        public static final short SUMMON_OWN = 3;
        public static final short SUMMON_OTHER = 4;
        public static final short UNKNOWN = 0xFF;
    }
}
