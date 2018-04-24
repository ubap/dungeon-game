package com.mygdx.game.dunegon.io;

import java.io.IOException;

public interface FileStream {

    long getU32() throws IOException;
    int getU16() throws IOException;
    short getU8() throws IOException;
    String getString() throws IOException;
    int tell() throws IOException;
    void seek(int position) throws IOException;
}
