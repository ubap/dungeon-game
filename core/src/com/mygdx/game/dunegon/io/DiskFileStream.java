package com.mygdx.game.dunegon.io;

import java.io.*;

public class DiskFileStream implements FileStream {
    private File file;
    private FileInputStream fileInputStream;


    public DiskFileStream(File file) {
        this.file = file;
        try {
            this.fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    @Override
    public long getU32() {
        byte[] buffer = new byte[4];
        try {
            fileInputStream.read(buffer);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        long val = buffer[3]<<24 & 0xFF000000L | buffer[2]<<16  & 0x00FF0000L |
                buffer[1]<<8 & 0x0000FF00L | buffer[0] & 0x000000FFL;

        return val;
    }

    @Override
    public int getU16() {
        byte[] buffer = new byte[2];
        try {
            fileInputStream.read(buffer);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        int val = buffer[1]<<8 & 0xFF00 | buffer[0] & 0xFF;
        return val;
    }

    @Override
    public short getU8() {
        byte[] buffer = new byte[1];
        try {
            fileInputStream.read(buffer);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        short val = (short) (buffer[0] & 0xFF);
        return val;
    }

    @Override
    public String getString() {
        int length = getU16();
        byte[] buffer = new byte[length];
        try {
            fileInputStream.read(buffer);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        String val = new String(buffer, 0, length);
        return val;
    }

    @Override
    public int tell() {
        try {
            return (int) fileInputStream.getChannel().position();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;
    }

    @Override
    public void seek(int position) {
        try {
             fileInputStream.getChannel().position(position);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
