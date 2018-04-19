package com.mygdx.game.dunegon.io;

public class FileStream {
    private byte[] bytes;
    private int position;

    public FileStream(byte[] bytes) {
        this.bytes = bytes;
        position = 0;
    }

    public long getU32() {
        long val = bytes[position + 3]<<24 & 0xFF000000L | bytes[position + 2]<<16  & 0x00FF0000L |
                bytes[position + 1]<<8 & 0x0000FF00L | bytes[position] & 0x000000FFL;

        position += 4;
        return val;
    }

    public int getU16() {
        int val = bytes[position + 1]<<8 & 0xFF00 | bytes[position] & 0xFF;
        position += 2;
        return val;
    }

    public short getU8() {
        short val = (short) (bytes[position] & 0xFF);
        position += 1;
        return val;
    }

    public String getString() {
        int length = getU16();
        String val = new String(bytes, position, length);
        position += length;
        return val;
    }

    public int tell() {
        return position;
    }

    public void seek(int position) {
        this.position = position;
    }
}
