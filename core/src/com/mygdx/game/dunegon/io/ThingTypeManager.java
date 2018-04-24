package com.mygdx.game.dunegon.io;

import com.mygdx.game.dunegon.game.ThingType;
import com.mygdx.game.graphics.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ThingTypeManager {
    private static ThingTypeManager INSTANCE;
    private static Logger LOGGER = LoggerFactory.getLogger(ThingTypeManager.class.getSimpleName());

    private ThingType[][] thingTypes;
    private long datSignature;
    private boolean loaded;

    private ThingTypeManager() {
        thingTypes = new ThingType[DatAttrs.ThingCategory.ThingLastCategory][];
    }

    /**
     * Thats some invalid singleton but it will help in thread safety.
     */
    public static void init() {
        LOGGER.info("init");
        if (INSTANCE == null) {
            INSTANCE = new ThingTypeManager();
        }
        INSTANCE.loaded = false;
    }

    public static ThingTypeManager getInstance() {
        return INSTANCE;
    }

    public ThingType getThingType(int id, int category) {
        return thingTypes[category][id];
    }

    public void loadDat(URI uri) {
        LOGGER.info("loadDat");
        try {
            FileStream fileStream = new DiskFileStream(new FileInputStream(new File(uri)));
            datSignature = fileStream.getU32();

            for (int i = 0; i < DatAttrs.ThingCategory.ThingLastCategory; i++) {
                int count = fileStream.getU16() + 1;
                thingTypes[i] = new ThingType[count];
            }

            for (int category = 0; category < DatAttrs.ThingCategory.ThingLastCategory; category++) {
                int firstId = 1;
                if (category == DatAttrs.ThingCategory.ThingCategoryItem) {
                    firstId = 100;
                }

                int count = thingTypes[category].length;
                for (int id = firstId; id < count; id++) {
                    ThingType thingType = readThing(id, category, fileStream);
                    thingTypes[category][id] = thingType;
                }
            }

            loaded = true;
        } catch (IOException ioe) {
            LOGGER.error("could not load dat", ioe);
            loaded = false;
        }

    }

    public static ThingType readThing(int id, int category, FileStream fileStream) throws IOException {
        // LOGGER.debug("Reading thingType for category: {}, item id: {}", category, id);
        ThingType thingType = new ThingType();
        thingType.setId(id);
        thingType.setCategory(category);
        boolean done = false;
        for (int i = 0; i < DatAttrs.Attribute.LAST_ATTR; i++) {
            short attribute = fileStream.getU8();

            if (DatAttrs.Attribute.LAST_ATTR == attribute) {
                done = true;
                break;
            }

            processAttribute(thingType, fileStream, attribute);
        }
        if (!done) {
            throw new RuntimeException("not done");
        }

        boolean hasFrameGroups = category == DatAttrs.ThingCategory.ThingCategoryCreature;
        short groupCount = hasFrameGroups ? fileStream.getU8() : 1;

        short totalSpritesCount = 0;
        short animationPhases = 0;
        List<Integer> spriteList = new ArrayList<Integer>();
        for (int group = 0; group < groupCount; group++) {
            if (hasFrameGroups) {
                short frameGroupType = fileStream.getU8();
            }
            short width = fileStream.getU8();
            short height = fileStream.getU8();
            Size size = new Size(width, height);
            if (width + height > 2) {
                short realSize = fileStream.getU8();
            }
            short nLayers = fileStream.getU8();
            short numPatternX = fileStream.getU8();
            short numPatternY = fileStream.getU8();
            short numPatternZ = fileStream.getU8();

            short groupAnimationPhases = fileStream.getU8();
            animationPhases += groupAnimationPhases;
            if (groupAnimationPhases > 1) {
                // animator, deserialize anim phases
                short async = fileStream.getU8();
                long loopCount = fileStream.getU32();
                short startPhase = fileStream.getU8();
                for (int phase = 0; phase < groupAnimationPhases; phase++) {
                    long minimum = fileStream.getU32();
                    long maxiumum = fileStream.getU32();
                }
            }

            int totalSprites = size.getArea() * nLayers * numPatternX * numPatternY * numPatternZ * groupAnimationPhases;

            if (totalSpritesCount + totalSprites > 4096) {
                throw new RuntimeException("has more than 4096 sprites count");
            }

            for (int sprite = totalSpritesCount; sprite < (totalSpritesCount + totalSprites); sprite++) {
                spriteList.add((int) fileStream.getU32());
            }

            totalSpritesCount += totalSprites;
            thingType.setSize(size);
            thingType.setPatternX(numPatternX);
            thingType.setPatternY(numPatternY);
            thingType.setPatternZ(numPatternZ);
            thingType.setLayers(nLayers);
        }

        thingType.setAnimationPhases(animationPhases);
        thingType.setSpriteIndexList(spriteList);


        return thingType;
    }

    private static void processAttribute(ThingType thingType, FileStream fileStream, short attribute) throws IOException {
        switch (attribute) {
            case DatAttrs.Attribute.GROUND: {
                LOGGER.trace("GROUND");
                thingType.setGround(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.GROUND_BORDER: {
                LOGGER.trace("GROUND_BORDER");
                thingType.setGroundBorder(true);
                break;
            }
            case DatAttrs.Attribute.ON_BOTTOM: {
                LOGGER.trace("ON_BOTTOM");
                thingType.setOnBottom(true);
                break;
            }
            case DatAttrs.Attribute.ON_TOP: {
                LOGGER.trace("ON_TOP");
                thingType.setOnTop(true);
                break;
            }
            case DatAttrs.Attribute.CONTAINER: {
                LOGGER.trace("CONTAINER");
                thingType.setContainer(true);
                break;
            }
            case DatAttrs.Attribute.FORCE_USE: {
                LOGGER.trace("FORCE_USE");
                thingType.setForceUse(true);
                break;
            }
            case DatAttrs.Attribute.MULTI_USE: {
                LOGGER.trace("MULTI_USE");
                thingType.setMultiUse(true);
                break;
            }
            case DatAttrs.Attribute.NOT_WALKABLE: {
                LOGGER.trace("NOT_WALKABLE");
                thingType.setNotWalkable(true);
                break;
            }
            case DatAttrs.Attribute.NOT_MOVEABLE: {
                LOGGER.trace("NOT_MOVEABLE");
                thingType.setNotMoveable(true);
                break;
            }
            case DatAttrs.Attribute.NOT_PATHABLE: {
                LOGGER.trace("NOT_PATHABLE");
                thingType.setNotPathable(true);
                break;
            }
            case DatAttrs.Attribute.HANGABLE: {
                LOGGER.trace("HANGABLE");
                thingType.setHangable(true);
                break;
            }
            case DatAttrs.Attribute.BLOCK_PROJECTILE: {
                LOGGER.trace("BLOCK_PROJECTILE");
                thingType.setBlockProjectile(true);
                break;
            }
            case DatAttrs.Attribute.PICKUPABLE: {
                LOGGER.trace("PICKUPABLE");
                thingType.setPickupable(true);
                break;
            }
            case DatAttrs.Attribute.DISPLACEMENT: {
                LOGGER.trace("DISPLACEMENT");
                // https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.7.4
                thingType.setDisplacement(true, fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LIGHT: {
                LOGGER.trace("LIGHT");
                thingType.setLight(true, fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.FULL_GROUND: {
                LOGGER.trace("FULL_GROUND");
                thingType.setFullGround(true);
                break;
            }
            case DatAttrs.Attribute.MARKET: {
                LOGGER.trace("MARKET");
                thingType.setMarket(true, fileStream.getU16(), fileStream.getU16(), fileStream.getU16(),
                        fileStream.getString(), fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.ELEVATION: {
                LOGGER.trace("ELEVATION");
                thingType.setElevation(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.MINIMAP_COLOR: {
                LOGGER.trace("MINIMAP_COLOR");
                thingType.setMinimapColor(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.CHARGABLE: {
                LOGGER.trace("CHARGABLE");
                thingType.setChargeable(true);
                break;
            }
            case DatAttrs.Attribute.STACKABLE: {
                LOGGER.trace("STACKABLE");
                thingType.setStackable(true);
                break;
            }
            case DatAttrs.Attribute.CLOTH: {
                LOGGER.trace("CLOTH");
                thingType.setCloth(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LYING_CORPSE: {
                LOGGER.trace("LYING_CORPSE");
                thingType.setLyingCorpse(true);
                break;
            }
            case DatAttrs.Attribute.TRANSCLUENT: {
                LOGGER.trace("TRANSCLUENT");
                thingType.setTranscluent(true);
                break;
            }
            case DatAttrs.Attribute.USABLE: {
                LOGGER.trace("USABLE");
                thingType.setUsable(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LOOK: {
                LOGGER.trace("LOOK");
                thingType.setLook(true);
                break;
            }
            case DatAttrs.Attribute.LENS_HELP: {
                LOGGER.trace("LENS_HELP");
                thingType.setLensHelp(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.FLUID_CONTAINER: {
                LOGGER.trace("FLUID_CONTAINER");
                thingType.setFluidContainer(true);
                break;
            }
            case DatAttrs.Attribute.HOOK_SOUTH: {
                LOGGER.trace("HOOK_SOUTH");
                thingType.setHookSouth(true);
                break;
            }
            case DatAttrs.Attribute.HOOK_EAST: {
                LOGGER.trace("HOOK_EAST");
                thingType.setHookEast(true);
                break;
            }
            case DatAttrs.Attribute.ROTATEABLE: {
                LOGGER.trace("ROTATEABLE");
                thingType.setRotateable(true);
                break;
            }
            case DatAttrs.Attribute.WRAPABLE: {
                LOGGER.trace("WRAPABLE");
                thingType.setWrapable(true);
                break;
            }
            case DatAttrs.Attribute.UNWRAPABLE: {
                LOGGER.trace("UNWRAPABLE");
                thingType.setUnwrapable(true);
                break;
            }
            case DatAttrs.Attribute.NO_MOVE_ANIM: {
                LOGGER.trace("NO_MOVE_ANIM");
                thingType.setNoMoveAnim(true);
                break;
            }
            case DatAttrs.Attribute.WRITABLE: {
                LOGGER.trace("WRITABLE");
                thingType.setWritable(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.WRITABLE_ONCE: {
                LOGGER.trace("WRITABLE_ONCE");
                thingType.setWritableOnce(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.SPLASH: {
                LOGGER.trace("SPLASH");
                thingType.setSplash(true);
                break;
            }
            case DatAttrs.Attribute.DONT_HIDE: {
                LOGGER.trace("DONT_HIDE");
                thingType.setDontHide(true);
                break;
            }
            case DatAttrs.Attribute.ANIMATE_ALWAYS: {
                LOGGER.trace("ANIMATE_ALWAYS");
                thingType.setAnimateAlways(true);
                break;
            }
            case DatAttrs.Attribute.TOP_EFFECT: {
                LOGGER.trace("TOP_EFFECT");
                thingType.setTopEffect(true);
                break;
            }
            default:
                LOGGER.info("Unrecognized attribute {}", String.format("0x%x", attribute));
                break;
        }
    }
}
