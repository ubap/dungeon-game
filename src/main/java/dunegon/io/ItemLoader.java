package dunegon.io;

import dunegon.game.Thing;
import dunegon.game.ThingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class ItemLoader {
    private static Logger sLogger = LoggerFactory.getLogger(ItemLoader.class.getSimpleName());

    public static void loadDat(ThingType[] thingType, URI uri) throws IOException {
        byte[] data = Files.readAllBytes(new File(uri).toPath());
        FileStream fileStream = new FileStream(data);

        long datSignature = fileStream.getU32();

        for (int i = 0; i < DatAttrs.ThingCategory.ThingLastCategory; i++) {
            int count = fileStream.getU16() + 1;
            thingType[i].initThingsArray(count);
        }

        for (int category = 0; category < DatAttrs.ThingCategory.ThingLastCategory; category++) {
            int firstId = 1;
            if (category == DatAttrs.ThingCategory.ThingCategoryItem) {
                firstId = 100;
            }

            int count = thingType[category].getThingCount();
            for (int id = firstId; id < count; id++) {
                Thing thing = readThing(id, category, fileStream);
                thingType[category].setThing(thing);
            }
        }

    }

    public static Thing readThing(int id, int category, FileStream fileStream) {
        sLogger.info("Reading thing for item id: {}", id);
        Thing thing = new Thing(id);
        boolean done = false;
        for (int i = 0; i < DatAttrs.Attribute.LAST_ATTR; i++) {
            short attribute = fileStream.getU8();

            if (DatAttrs.Attribute.LAST_ATTR == attribute) {
                done = true;
                break;
            }

            processAttribute(thing, fileStream, attribute);
        }
        if (!done) {
            throw new RuntimeException("not done");
        }

        boolean hasFrameGroups = category == DatAttrs.ThingCategory.ThingCategoryCreature;
        short groupCount = hasFrameGroups ? fileStream.getU8() : 1;

        short totalSpritesCount = 0;
        short animationPhases = 0;
        for (int group = 0; group < groupCount; group++) {
            if (hasFrameGroups) {
                short frameGroupType = fileStream.getU8();
            }
            short width = fileStream.getU8();
            short height = fileStream.getU8();
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

            int totalSprites = nLayers * width * height * numPatternX * numPatternY * numPatternZ * groupAnimationPhases;

            if (totalSpritesCount + totalSprites > 4096) {
                throw new RuntimeException("has more than 4096 sprites count");
            }

            for (int sprite = totalSpritesCount; sprite < (totalSpritesCount + totalSprites); sprite++) {
                fileStream.getU32();
            }

            totalSpritesCount += totalSprites;
        }

        return thing;
    }

    private static void processAttribute(Thing thing, FileStream fileStream, short attribute) {
        switch (attribute) {
            case DatAttrs.Attribute.GROUND: {
                sLogger.info("GROUND");
                thing.setGround(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.GROUND_BORDER: {
                sLogger.info("GROUND_BORDER");
                thing.setGroundBorder(true);
                break;
            }
            case DatAttrs.Attribute.ON_BOTTOM: {
                sLogger.info("ON_BOTTOM");
                thing.setOnBottom(true);
                break;
            }
            case DatAttrs.Attribute.ON_TOP: {
                sLogger.info("ON_TOP");
                thing.setOnTop(true);
                break;
            }
            case DatAttrs.Attribute.CONTAINER: {
                sLogger.info("CONTAINER");
                thing.setContainer(true);
                break;
            }
            case DatAttrs.Attribute.FORCE_USE: {
                sLogger.info("FORCE_USE");
                thing.setForceUse(true);
                break;
            }
            case DatAttrs.Attribute.MULTI_USE: {
                sLogger.info("MULTI_USE");
                thing.setMultiUse(true);
                break;
            }
            case DatAttrs.Attribute.NOT_WALKABLE: {
                sLogger.info("NOT_WALKABLE");
                thing.setNotWalkable(true);
                break;
            }
            case DatAttrs.Attribute.NOT_MOVEABLE: {
                sLogger.info("NOT_MOVEABLE");
                thing.setNotMoveable(true);
                break;
            }
            case DatAttrs.Attribute.NOT_PATHABLE: {
                sLogger.info("NOT_PATHABLE");
                thing.setNotPathable(true);
                break;
            }
            case DatAttrs.Attribute.HANGABLE: {
                sLogger.info("HANGABLE");
                thing.setHangable(true);
                break;
            }
            case DatAttrs.Attribute.BLOCK_PROJECTILE: {
                sLogger.info("BLOCK_PROJECTILE");
                thing.setBlockProjectile(true);
                break;
            }
            case DatAttrs.Attribute.PICKUPABLE: {
                sLogger.info("PICKUPABLE");
                thing.setPickupable(true);
                break;
            }
            case DatAttrs.Attribute.DISPLACEMENT: {
                sLogger.info("DISPLACEMENT");
                // https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.7.4
                thing.setDisplacement(true, fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LIGHT: {
                sLogger.info("LIGHT");
                thing.setLight(true, fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.FULL_GROUND: {
                sLogger.info("FULL_GROUND");
                break;
            }
            case DatAttrs.Attribute.MARKET: {
                sLogger.info("MARKET");
                thing.setMarket(true, fileStream.getU16(), fileStream.getU16(), fileStream.getU16(),
                        fileStream.getString(), fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.ELEVATION: {
                sLogger.info("ELEVATION");
                thing.setElevation(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.MINIMAP_COLOR: {
                sLogger.info("MINIMAP_COLOR");
                thing.setMinimapColor(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.CHARGABLE: {
                sLogger.info("CHARGABLE");
                thing.setChargeable(true);
                break;
            }
            case DatAttrs.Attribute.STACKABLE: {
                sLogger.info("STACKABLE");
                thing.setStackable(true);
                break;
            }
            case DatAttrs.Attribute.CLOTH: {
                sLogger.info("CLOTH");
                thing.setCloth(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LYING_CORPSE: {
                sLogger.info("LYING_CORPSE");
                thing.setLyingCorpse(true);
                break;
            }
            case DatAttrs.Attribute.TRANSCLUENT: {
                sLogger.info("TRANSCLUENT");
                thing.setTranscluent(true);
                break;
            }
            case DatAttrs.Attribute.USABLE: {
                sLogger.info("USABLE");
                thing.setUsable(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LOOK: {
                sLogger.info("LOOK");
                thing.setLook(true);
                break;
            }
            case DatAttrs.Attribute.LENS_HELP: {
                sLogger.info("LENS_HELP");
                thing.setLensHelp(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.FLUID_CONTAINER: {
                sLogger.info("FLUID_CONTAINER");
                thing.setFluidContainer(true);
                break;
            }
            case DatAttrs.Attribute.HOOK_SOUTH: {
                sLogger.info("HOOK_SOUTH");
                thing.setHookSouth(true);
                break;
            }
            case DatAttrs.Attribute.HOOK_EAST: {
                sLogger.info("HOOK_EAST");
                thing.setHookEast(true);
                break;
            }
            case DatAttrs.Attribute.ROTATEABLE: {
                sLogger.info("ROTATEABLE");
                thing.setRotateable(true);
                break;
            }
            case DatAttrs.Attribute.WRAPABLE: {
                sLogger.info("WRAPABLE");
                thing.setWrapable(true);
                break;
            }
            case DatAttrs.Attribute.UNWRAPABLE: {
                sLogger.info("UNWRAPABLE");
                thing.setUnwrapable(true);
                break;
            }
            case DatAttrs.Attribute.NO_MOVE_ANIM: {
                sLogger.info("NO_MOVE_ANIM");
                thing.setNoMoveAnim(true);
                break;
            }
            case DatAttrs.Attribute.WRITABLE: {
                sLogger.info("WRITABLE");
                thing.setWritable(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.WRITABLE_ONCE: {
                sLogger.info("WRITABLE_ONCE");
                thing.setWritableOnce(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.SPLASH: {
                sLogger.info("SPLASH");
                thing.setSplash(true);
                break;
            }
            case DatAttrs.Attribute.DONT_HIDE: {
                sLogger.info("DONT_HIDE");
                thing.setDontHide(true);
                break;
            }
            case DatAttrs.Attribute.ANIMATE_ALWAYS: {
                sLogger.info("ANIMATE_ALWAYS");
                thing.setAnimateAlways(true);
                break;
            }
            case DatAttrs.Attribute.TOP_EFFECT: {
                sLogger.info("TOP_EFFECT");
                thing.setTopEffect(true);
                break;
            }
            default:
                sLogger.info("Unrecognized attribute {}", String.format("0x%x", attribute));
                break;
        }
    }
}
