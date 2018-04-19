package com.mygdx.game.dunegon.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class InputMessage {
    private ByteBuffer byteBuffer;
    private int messageSize;

    public InputMessage() {
        byteBuffer = ByteBuffer.allocate(65535);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        reset();
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    public short getU8() {
        byte[] bytes = byteBuffer.array();
        int position = byteBuffer.position();
        short val = (short) (bytes[position] & ((short) 0xFF));
        byteBuffer.position(position + 1);
        return val;
    }

    public int getU16() {
        byte[] bytes = byteBuffer.array();
        int position = byteBuffer.position();
        int val =  bytes[position + 1]<<8 & 0xFF00 | bytes[position] & 0xFF;
        byteBuffer.position(position + 2);
        return val;
    }

    public int peekU16() {
        byte[] bytes = byteBuffer.array();
        int position = byteBuffer.position();
        int val =  bytes[position + 1]<<8 & 0xFF00 | bytes[position] & 0xFF;
        return val;
    }

    public long getU32() {
        byte[] bytes = byteBuffer.array();
        int position = byteBuffer.position();
        long val = bytes[position + 3]<<24 & 0xFF000000 | bytes[position + 2]<<16 & 0xFF0000
                | bytes[position + 1]<<8 & 0xFF00 | bytes[position] & 0xFF;
        byteBuffer.position(position + 4);
        return val;
    }

    /** Due to java limitations U64 is missing. Double could be used instead. **/
    public long get64() {
        long v1 = getU32();
        long v2 = getU32();
        return v1 + v2 * 0xFFFFFFFFL;
    }

    public double getDouble() {
        int precision = byteBuffer.get();
        long v = getU32() - Integer.MAX_VALUE;
        return (v / Math.pow(10, precision));
    }

    public String getString() {
        int length = byteBuffer.getShort();
        String val = new String(byteBuffer.array(), byteBuffer.position(), length);
        byteBuffer.position(byteBuffer.position() + length);
        return val;
    }

    public boolean hasMore() {
        return byteBuffer.position() < messageSize;
    }

    public void reset() {
        messageSize = 0;
        byteBuffer.position(0);
    }

    public int getPosition() {
        return byteBuffer.position();
    }

    public byte[] getBuffer() {
        return byteBuffer.array();
    }
}
