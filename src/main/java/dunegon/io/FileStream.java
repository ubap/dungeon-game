package dunegon.io;

public class FileStream {
    private byte[] mBytes;
    private int mPosition;

    public FileStream(byte[] bytes) {
        mBytes = bytes;
        mPosition = 0;
    }

    public long getU32() {
        long val =  mBytes[mPosition + 3]<<24 & 0xFF000000L | mBytes[mPosition + 2]<<16  & 0x00FF0000L |
                mBytes[mPosition + 1]<<8 & 0x0000FF00L | mBytes[mPosition] & 0x000000FFL;

        mPosition += 4;
        return val;
    }

    public int getU16() {
        int val =  mBytes[mPosition + 1]<<8 & 0xFF00 | mBytes[mPosition] & 0xFF;
        mPosition += 2;
        return val;
    }

    public short getU8() {
        short val = (short) (mBytes[mPosition] & 0xFF);
        mPosition += 1;
        return val;
    }

    public String getString() {
        int length = getU16();
        String val = new String(mBytes, mPosition, length);
        mPosition += length;
        return val;
    }
}
