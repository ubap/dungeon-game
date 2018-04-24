package com.mygdx.game.dunegon.io;

public interface FileStream {

    long getU32();
    int getU16();
    short getU8();
    String getString();
    int tell();
    void seek(int position);
}
