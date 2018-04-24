package com.mygdx.game.dunegon.io;

import java.io.FileInputStream;
import java.io.IOException;

public class DiskFileStream implements FileStream {
    private FileInputStream fileInputStream;


    public DiskFileStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    @Override
    public long getU32() throws IOException {
        byte[] buffer = new byte[4];
        fileInputStream.read(buffer);
        long val = buffer[3]<<24 & 0xFF000000L | buffer[2]<<16  & 0x00FF0000L |
                buffer[1]<<8 & 0x0000FF00L | buffer[0] & 0x000000FFL;

        return val;
    }

    @Override
    public int getU16() throws IOException  {
        byte[] buffer = new byte[2];
        fileInputStream.read(buffer);
        int val = buffer[1]<<8 & 0xFF00 | buffer[0] & 0xFF;
        return val;
    }

    @Override
    public short getU8() throws IOException  {
        byte[] buffer = new byte[1];
        fileInputStream.read(buffer);
        short val = (short) (buffer[0] & 0xFF);
        return val;
    }

    @Override
    public String getString() throws IOException  {
        int length = getU16();
        byte[] buffer = new byte[length];
        fileInputStream.read(buffer);
        String val = new String(buffer, 0, length);
        return val;
    }

    @Override
    public int tell() throws IOException  {
        return (int) fileInputStream.getChannel().position();
    }

    @Override
    public void seek(int position) throws IOException  {
         fileInputStream.getChannel().position(position);
    }
}
