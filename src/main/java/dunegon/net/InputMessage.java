package dunegon.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// TODO: getU64
public class InputMessage {
    private ByteBuffer mByteBuffer;
    private int mMessageSize;

    public InputMessage() {
        mByteBuffer = ByteBuffer.allocate(65535);
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        reset();
    }

    public void setMessageSize(int messageSize) {
        mMessageSize = messageSize;
    }

    public byte getU8() {
        return mByteBuffer.get();
    }

    public int getU16() {
        return mByteBuffer.getShort();
    }

    public int peekU16() {
        byte[] bytes = mByteBuffer.array();
        int position = mByteBuffer.position();
        int val =  bytes[position + 1]<<8 & 0xFF00 | bytes[position] & 0xFF;
        return val;
    }

    public int getU32() {
        return mByteBuffer.getInt();
    }

    public double getDouble() {
        int precision = mByteBuffer.get();
        int v = getU32() - Integer.MAX_VALUE;
        return (v / Math.pow(10, precision));
    }

    public String getString() {
        int length = mByteBuffer.getShort();
        String val = new String(mByteBuffer.array(), mByteBuffer.position(), length);
        mByteBuffer.position(mByteBuffer.position() + length);
        return val;
    }

    public boolean hasMore() {
        return mByteBuffer.position() < mMessageSize;
    }

    public void reset() {
        mMessageSize = 0;
        mByteBuffer.position(0);
    }

    public int getPosition() {
        return mByteBuffer.position();
    }

    public byte[] getBuffer() {
        return mByteBuffer.array();
    }
}
