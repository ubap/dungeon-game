package com.mygdx.game.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.utils.TimeDifferenceCounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TextureAtlas {
    private static Logger LOGGER = LoggerFactory.getLogger(TextureAtlas.class.getSimpleName());
    private static TextureAtlas INSTANCE;

    private String directoryPath;
    private String packFileName;

    private Texture page;

    private Map<String, AtlasItemMetadata> atlasItemMetadataMap;
    private Map<String, TextureRegion> textureRegionMap;

    public static void init() {
        INSTANCE = new TextureAtlas();
        INSTANCE.directoryPath = "atlas/";
        INSTANCE.atlasItemMetadataMap = new HashMap<String, AtlasItemMetadata>();
        INSTANCE.textureRegionMap = new HashMap<String, TextureRegion>();
    }

    public static TextureAtlas getInstance() {
        return INSTANCE;
    }


    public void loadAtlas() {
        LOGGER.info("Starting loading");
        loadMetadata();

        this.page = new Texture(INSTANCE.directoryPath + packFileName);

        for (AtlasItemMetadata atlasItemMetadata : this.atlasItemMetadataMap.values()) {
            TextureRegion textureRegion = new TextureRegion(this.page, atlasItemMetadata.getPositionX(), atlasItemMetadata.getPositionY(),
                    atlasItemMetadata.getSizeX(), atlasItemMetadata.getSizeY());
            INSTANCE.textureRegionMap.put(atlasItemMetadata.getId(), textureRegion);
        }

        LOGGER.info("loading took {} ms", TimeDifferenceCounter.getInstance().getTimeDifference(this));
    }

    public TextureRegion getTextureRegion(String id) {
        if (!this.textureRegionMap.containsKey(id)) {
            return null;
        }
        return this.textureRegionMap.get(id);
    }

    private void loadMetadata() {

        TimeDifferenceCounter.getInstance().startCounter(this);
        try {
            File metadataFile = new File(this.directoryPath + "pack.atlas");

            Scanner scanner = new Scanner(metadataFile);

            String firstLine = scanner.nextLine();
            if (!firstLine.isEmpty()) {
                throw new RuntimeException();
            }
            this.packFileName = scanner.nextLine();
            String size = scanner.nextLine();
            String format = scanner.nextLine();
            String filter = scanner.nextLine();
            String repeat = scanner.nextLine();

            while (scanner.hasNext()) {
                AtlasItemMetadata atlasItemMetadata = loadItemMetadata(scanner);
                this.atlasItemMetadataMap.put(atlasItemMetadata.getId(), atlasItemMetadata);
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private AtlasItemMetadata loadItemMetadata(Scanner scanner) {
        String id = scanner.next();

        ensureKeyword("rotate:", scanner.next());
        String rotate = scanner.next();

        ensureKeyword("xy:", scanner.next());
        String positionX = scanner.next().split(",")[0]; // x
        String positionY = scanner.next(); // y

        ensureKeyword("size:", scanner.next());
        String sizeX = scanner.next().split(",")[0]; // x
        String sizeY = scanner.next(); // y

        ensureKeyword("orig:", scanner.next());
        String origX = scanner.next().split(",")[0]; // x
        String origY = scanner.next(); // y

        ensureKeyword("offset:", scanner.next());
        String offsetX = scanner.next().split(",")[0]; // x
        String offsetY = scanner.next(); // y

        ensureKeyword("index:", scanner.next());
        String index = scanner.next(); // value

        return new AtlasItemMetadata(id,
                Boolean.parseBoolean(rotate),
                Integer.parseInt(positionX),
                Integer.parseInt(positionY),
                Integer.parseInt(sizeX),
                Integer.parseInt(sizeY),
                Integer.parseInt(origX),
                Integer.parseInt(origY),
                Integer.parseInt(offsetX),
                Integer.parseInt(offsetY),
                Integer.parseInt(index));

    }

    private void ensureKeyword(String desired, String ensured) {
        if (!desired.equals(ensured)) {
            throw new RuntimeException("incorrect keyword, metadat file corrupted");
        }
    }
}
