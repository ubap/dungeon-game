package game.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class InputMessage {
    private ByteBuffer mByteBuffer;
    private int mWritePos;

    public InputMessage() {
        mByteBuffer = ByteBuffer.allocate(65535);
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mWritePos = 0;
    }

    public byte getU8() {
        return mByteBuffer.get();
    }

    public short getU16() {
        return mByteBuffer.getShort();
    }

    public int getU32() {
        return mByteBuffer.getInt();
    }

    public String getString() {
        int length = mByteBuffer.getShort();
        return new String( mByteBuffer.array(), mByteBuffer.position(), length);
    }

    public void reset() {

    }

    public int getPosition() {
        return mByteBuffer.position();
    }

    public byte[] getBuffer() {
        return mByteBuffer.array();
    }
}
