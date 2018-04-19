package com.mygdx.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;

import javax.xml.soap.Text;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class SpriteManager {
    private static SpriteManager INSTANCE;

    private static final int SPRITE_SIZE = 32;
    private static final int SPRITE_DATA_SIZE = SPRITE_SIZE * SPRITE_SIZE * 4;

    private FileStream spritesFile;
    private int spritesOffset;
    private long spritesCount;
    private long signature;
    private boolean loaded;

    private SpriteManager() {
    }

    public static void init() {
        if (SpriteManager.INSTANCE == null) {
            SpriteManager.INSTANCE = new SpriteManager();
        }
    }

    public static SpriteManager getInstance() {
        return SpriteManager.INSTANCE;
    }

    public void loadSpr(URI uri) {
        spritesCount = 0;
        signature = 0;
        loaded = false;

        try {
            byte[] data = Files.readAllBytes(new File(uri).toPath());
            spritesFile = new FileStream(data);
            signature = spritesFile.getU32();
            spritesCount = spritesFile.getU32();
            spritesOffset = spritesFile.tell();
            loaded = true;

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void unloadSpr() {
        spritesFile = null;
        loaded = false;
    }

    public Texture getSpriteImage(int id) {
        // todo
        if (id == 0 || spritesFile == null) {
            return null;
        }

        spritesFile.seek(((id-1) * 4) + spritesOffset);

        int spriteAddress = (int) spritesFile.getU32();
        if (spriteAddress <= 0) {
            return null;
        }

        spritesFile.seek(spriteAddress);

        // skip color key
        spritesFile.getU8();
        spritesFile.getU8();
        spritesFile.getU8();

        int pixelDataSize = spritesFile.getU16();

        Gdx2DPixmap gdx2DPixmap = new Gdx2DPixmap(SPRITE_SIZE, SPRITE_SIZE, Gdx2DPixmap.GDX2D_FORMAT_RGBA8888);

        ByteBuffer byteBuffer = gdx2DPixmap.getPixels();

//        for (int x = 0; x < SPRITE_SIZE; x++) {
//            for (int y = 0; y < SPRITE_SIZE; y++) {
//                byteBuffer.put((byte) 0x00);
//                byteBuffer.put((byte) 0xff);
//                byteBuffer.put((byte) 0x00);
//                byteBuffer.put((byte) 0xff);
//            }
//        }

        int writePos = 0;
        int read = 0;
        final int channels = 3;

        while (read < pixelDataSize && writePos < SPRITE_DATA_SIZE) {
            int transparentPixels = spritesFile.getU16();
            int coloredPixels = spritesFile.getU16();

            for (int i = 0; i < transparentPixels && writePos < SPRITE_DATA_SIZE; i++) {
                setFourPixelsToZero(byteBuffer);
                writePos += 4;
            }

            for (int i = 0; i < coloredPixels && writePos < SPRITE_DATA_SIZE; i++) {
                byte r = (byte) spritesFile.getU8();
                byte g = (byte) spritesFile.getU8();
                byte b = (byte) spritesFile.getU8();
                byte a = (byte) 0xFF;

                byteBuffer.put(r);
                byteBuffer.put(g);
                byteBuffer.put(b);
                byteBuffer.put(a);
                writePos += 4;
            }

            read += 4 + (channels + coloredPixels);
        }

        while (writePos < SPRITE_DATA_SIZE) {
            setFourPixelsToZero(byteBuffer);
            writePos += 4;
        }

        byteBuffer.position(0);


        Pixmap pixmap = new Pixmap(gdx2DPixmap);
        return new Texture(pixmap);


    }

    private void setFourPixelsToZero(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 0);
    }

}