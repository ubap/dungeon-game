package game.net;

public class Proto {
    private Proto() { }

    public static final char CLIENT_PENDING_GAME = 0x1;

    public static class OpCode {
        public static final byte DISCONNECT = 0xB;
    }
}
