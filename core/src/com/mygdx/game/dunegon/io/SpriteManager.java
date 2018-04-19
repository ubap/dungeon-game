package com.mygdx.game.dunegon.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class SpriteManager {
    private static SpriteManager INSTANCE;
    private static Logger LOGGER = LoggerFactory.getLogger(SpriteManager.class.getSimpleName());

    private FileStream spritesFile;
    private int spritesOffset;
    private long spritesCount;
    private long signature;
    private boolean loaded;

    private SpriteManager() {
    }

    public static void init() {
        LOGGER.info("init");
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

    public void getSpriteImage(int id) {
        // todo
    }

}
