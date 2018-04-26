package com.mygdx.game.dunegon.io;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class SpriteManager {
    private static Logger LOGGER = LoggerFactory.getLogger(SpriteManager.class.getSimpleName());
    private static SpriteManager INSTANCE;

    private static final int SPRITE_SIZE = 32;
    private static final int SPRITE_DATA_SIZE = SPRITE_SIZE * SPRITE_SIZE * 4;

    private FileStream spritesFile;
    private int spritesOffset;
    private long spritesCount;
    private long signature;
    private boolean loaded;

    public Set<Integer> loadedSprites = new HashSet<Integer>();

    private SpriteManager() {
    }

    public static void init() {
        if (SpriteManager.INSTANCE == null) {
            SpriteManager.INSTANCE = new SpriteManager();
        }
        INSTANCE.loaded = false;
        INSTANCE.spritesCount = 0;
        INSTANCE.signature = 0;
    }

    public static SpriteManager getInstance() {
        return SpriteManager.INSTANCE;
    }

    public void loadSpr(URI uri) {
        try {
            spritesFile = new DiskFileStream(new FileInputStream(new File(uri)));
            signature = spritesFile.getU32();
            spritesCount = spritesFile.getU32();
            spritesOffset = spritesFile.tell();
            loaded = true;
        } catch (IOException ioe) {
            loaded = false;
            LOGGER.error("could not load spr", ioe);
        }
    }

    public Pixmap getSpriteImage(int id) {
        // todo
        if (id == 0 || spritesFile == null) {
            return null;
        }
        Pixmap pixmap = null;
        try {
            spritesFile.seek(((id - 1) * 4) + spritesOffset);

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

                read += 4 + (channels * coloredPixels);
            }

        while (writePos < SPRITE_DATA_SIZE) {
            setFourPixelsToZero(byteBuffer);
            writePos += 4;
        }

        byteBuffer.position(0);


        pixmap = new Pixmap(gdx2DPixmap);

        this.loadedSprites.add(id);
        byte[] bytes = PNG.toPNG(pixmap);
        FileOutputStream fos = new FileOutputStream(String.format("sprites%s%d.png", File.separator, id));
        fos.write(bytes);
        fos.close();

        } catch (IOException ioe) {
            LOGGER.error("getSpriteImage failed", ioe);
            loaded = false;
        }

        return pixmap;
    }

    private void setFourPixelsToZero(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 0);
    }

}