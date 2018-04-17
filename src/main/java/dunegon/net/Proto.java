package dunegon.net;

public class Proto {
    private Proto() { }

    public static final char CLIENT_PENDING_GAME = 0x1;

    public static class OpCode {
        public static final byte DISCONNECT = 0xB;
        public static final byte MOTD = 0x14;
        public static final byte SESSION_KEY = 0x28;
        public static final byte CHAR_LIST = 0x64;


        // gameworld
        public static final byte GAMEWORLD_PING = 0x1D;
        public static final byte GAMEWORLD_PING_BACK = 0x1E;
        public static final byte GAMEWORLD_FIRST_PACKET = 0x1F;
        public static final byte GAMESERVER_LOGIN_SUCCESS = 0x17;

        public static final byte CREATURE_SAY = (byte) 0xAA;

        public static final byte PENDING_STATE = 0xA;
        public static final byte ENTER_WORLD = 0x0F;



        public static final byte MAP_DESCRIPTION = 0x64;
        public static final byte SET_INVENTORY = 0x78;
        public static final byte DELETE_INVENTORY = 0x79;
        public static final byte GRAPHICAL_EFFECT = (byte) 0x83;
        public static final byte PLAYER_STATS = (byte) 0xA0;
        public static final byte PLAYER_SKILLS = (byte) 0xA1;


        public static class Send {

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
