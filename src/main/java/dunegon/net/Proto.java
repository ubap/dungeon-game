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

        public static final byte PENDING_STATE = 0xA;
        public static final byte ENTER_WORLD = 0x0F;

        public static final byte MAP_DESCRIPTION = 0x64;
    }
}
