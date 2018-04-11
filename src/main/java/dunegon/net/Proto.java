package dunegon.net;

public class Proto {
    private Proto() { }

    public static final char CLIENT_PENDING_GAME = 0x1;

    public static class OpCode {
        public static final byte DISCONNECT = 0xB;
        public static final byte MOTD = 0x14;
        public static final byte SESSION_KEY = 0x28;
        public static final byte CHAR_LIST = 0x64;

        public static final byte GAMEWORLD_FIRST_PACKET = 0x1F;
    }
}
