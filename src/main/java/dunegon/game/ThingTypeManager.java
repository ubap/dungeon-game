package dunegon.game;

import dunegon.io.DatAttrs;
import dunegon.io.FileStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class ThingTypeManager {
    private static ThingTypeManager sThingTypeManager;
    private static Logger sLogger = LoggerFactory.getLogger(ThingTypeManager.class.getSimpleName());

    private ThingType[][] thingTypes;

    private ThingTypeManager() {
        thingTypes = new ThingType[DatAttrs.ThingCategory.ThingLastCategory][];
    }

    /**
     * Thats some invalid singleton but it will help in thread safety.
     */
    public static void init() {
        sLogger.info("init");
        if (sThingTypeManager == null) {
            sThingTypeManager = new ThingTypeManager();
        }
    }

    public static ThingTypeManager getInstance() {
        return sThingTypeManager;
    }

    public ThingType getThingType(int id, int category) {
        return thingTypes[category][id];
    }

    public void loadDat(URI uri) throws IOException {
        sLogger.info("loadDat");

        byte[] data = Files.readAllBytes(new File(uri).toPath());
        FileStream fileStream = new FileStream(data);

        long datSignature = fileStream.getU32();


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

    }

    public static ThingType readThing(int id, int category, FileStream fileStream) {
        sLogger.debug("Reading thingType for item id: {}", id);
        ThingType thingType = new ThingType();
        thingType.setId(id);
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

        thingType.setAnimationPhases(animationPhases);

        return thingType;



    }

    private static void processAttribute(ThingType thingType, FileStream fileStream, short attribute) {
        switch (attribute) {
            case DatAttrs.Attribute.GROUND: {
                sLogger.trace("GROUND");
                thingType.setGround(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.GROUND_BORDER: {
                sLogger.trace("GROUND_BORDER");
                thingType.setGroundBorder(true);
                break;
            }
            case DatAttrs.Attribute.ON_BOTTOM: {
                sLogger.trace("ON_BOTTOM");
                thingType.setOnBottom(true);
                break;
            }
            case DatAttrs.Attribute.ON_TOP: {
                sLogger.trace("ON_TOP");
                thingType.setOnTop(true);
                break;
            }
            case DatAttrs.Attribute.CONTAINER: {
                sLogger.trace("CONTAINER");
                thingType.setContainer(true);
                break;
            }
            case DatAttrs.Attribute.FORCE_USE: {
                sLogger.trace("FORCE_USE");
                thingType.setForceUse(true);
                break;
            }
            case DatAttrs.Attribute.MULTI_USE: {
                sLogger.trace("MULTI_USE");
                thingType.setMultiUse(true);
                break;
            }
            case DatAttrs.Attribute.NOT_WALKABLE: {
                sLogger.trace("NOT_WALKABLE");
                thingType.setNotWalkable(true);
                break;
            }
            case DatAttrs.Attribute.NOT_MOVEABLE: {
                sLogger.trace("NOT_MOVEABLE");
                thingType.setNotMoveable(true);
                break;
            }
            case DatAttrs.Attribute.NOT_PATHABLE: {
                sLogger.trace("NOT_PATHABLE");
                thingType.setNotPathable(true);
                break;
            }
            case DatAttrs.Attribute.HANGABLE: {
                sLogger.trace("HANGABLE");
                thingType.setHangable(true);
                break;
            }
            case DatAttrs.Attribute.BLOCK_PROJECTILE: {
                sLogger.trace("BLOCK_PROJECTILE");
                thingType.setBlockProjectile(true);
                break;
            }
            case DatAttrs.Attribute.PICKUPABLE: {
                sLogger.trace("PICKUPABLE");
                thingType.setPickupable(true);
                break;
            }
            case DatAttrs.Attribute.DISPLACEMENT: {
                sLogger.trace("DISPLACEMENT");
                // https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.7.4
                thingType.setDisplacement(true, fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LIGHT: {
                sLogger.trace("LIGHT");
                thingType.setLight(true, fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.FULL_GROUND: {
                sLogger.trace("FULL_GROUND");
                break;
            }
            case DatAttrs.Attribute.MARKET: {
                sLogger.trace("MARKET");
                thingType.setMarket(true, fileStream.getU16(), fileStream.getU16(), fileStream.getU16(),
                        fileStream.getString(), fileStream.getU16(), fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.ELEVATION: {
                sLogger.trace("ELEVATION");
                thingType.setElevation(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.MINIMAP_COLOR: {
                sLogger.trace("MINIMAP_COLOR");
                thingType.setMinimapColor(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.CHARGABLE: {
                sLogger.trace("CHARGABLE");
                thingType.setChargeable(true);
                break;
            }
            case DatAttrs.Attribute.STACKABLE: {
                sLogger.trace("STACKABLE");
                thingType.setStackable(true);
                break;
            }
            case DatAttrs.Attribute.CLOTH: {
                sLogger.trace("CLOTH");
                thingType.setCloth(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LYING_CORPSE: {
                sLogger.trace("LYING_CORPSE");
                thingType.setLyingCorpse(true);
                break;
            }
            case DatAttrs.Attribute.TRANSCLUENT: {
                sLogger.trace("TRANSCLUENT");
                thingType.setTranscluent(true);
                break;
            }
            case DatAttrs.Attribute.USABLE: {
                sLogger.trace("USABLE");
                thingType.setUsable(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.LOOK: {
                sLogger.trace("LOOK");
                thingType.setLook(true);
                break;
            }
            case DatAttrs.Attribute.LENS_HELP: {
                sLogger.trace("LENS_HELP");
                thingType.setLensHelp(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.FLUID_CONTAINER: {
                sLogger.trace("FLUID_CONTAINER");
                thingType.setFluidContainer(true);
                break;
            }
            case DatAttrs.Attribute.HOOK_SOUTH: {
                sLogger.trace("HOOK_SOUTH");
                thingType.setHookSouth(true);
                break;
            }
            case DatAttrs.Attribute.HOOK_EAST: {
                sLogger.trace("HOOK_EAST");
                thingType.setHookEast(true);
                break;
            }
            case DatAttrs.Attribute.ROTATEABLE: {
                sLogger.trace("ROTATEABLE");
                thingType.setRotateable(true);
                break;
            }
            case DatAttrs.Attribute.WRAPABLE: {
                sLogger.trace("WRAPABLE");
                thingType.setWrapable(true);
                break;
            }
            case DatAttrs.Attribute.UNWRAPABLE: {
                sLogger.trace("UNWRAPABLE");
                thingType.setUnwrapable(true);
                break;
            }
            case DatAttrs.Attribute.NO_MOVE_ANIM: {
                sLogger.trace("NO_MOVE_ANIM");
                thingType.setNoMoveAnim(true);
                break;
            }
            case DatAttrs.Attribute.WRITABLE: {
                sLogger.trace("WRITABLE");
                thingType.setWritable(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.WRITABLE_ONCE: {
                sLogger.trace("WRITABLE_ONCE");
                thingType.setWritableOnce(true, fileStream.getU16());
                break;
            }
            case DatAttrs.Attribute.SPLASH: {
                sLogger.trace("SPLASH");
                thingType.setSplash(true);
                break;
            }
            case DatAttrs.Attribute.DONT_HIDE: {
                sLogger.trace("DONT_HIDE");
                thingType.setDontHide(true);
                break;
            }
            case DatAttrs.Attribute.ANIMATE_ALWAYS: {
                sLogger.trace("ANIMATE_ALWAYS");
                thingType.setAnimateAlways(true);
                break;
            }
            case DatAttrs.Attribute.TOP_EFFECT: {
                sLogger.trace("TOP_EFFECT");
                thingType.setTopEffect(true);
                break;
            }
            default:
                sLogger.info("Unrecognized attribute {}", String.format("0x%x", attribute));
                break;
        }
    }
}
