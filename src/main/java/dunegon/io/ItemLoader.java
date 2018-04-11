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
                readThing(id, category, fileStream);

                continue;
            }
        }

    }

    public static Thing readThing(int id, int category, FileStream fileStream) {
        sLogger.info("Reading thing for item id: {}", id);
        Thing thing = new Thing(id);
        boolean done = false;
        for (int i = 0; i < DatAttrs.THING_LAST_ATTR; i++) {
            short attribute = fileStream.getU8();

            if (DatAttrs.THING_LAST_ATTR == attribute) {
                done = true;
                break;
            }

            processAttribute(fileStream, attribute);
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
            if (width > 0 || height > 0) {
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

            int totalSprites = width * height * nLayers * numPatternX * numPatternY * numPatternZ * groupAnimationPhases;

            if (totalSpritesCount + totalSprites > 4096) {
                throw new RuntimeException("has more than 4096 sprites count");
            }

            for (int sprite = totalSpritesCount; sprite < (totalSpritesCount + totalSprites); sprite++) {
                fileStream.getU16();
            }

            totalSpritesCount += totalSprites;
        }

        return thing;
    }

    private static void processAttribute(FileStream fileStream, short attribute) {
        switch (attribute) {
            case DatAttrs.Attribute.GROUND:
                sLogger.info("GROUND");
                fileStream.getU16();
                break;
            case DatAttrs.Attribute.GROUND_BORDER:
                sLogger.info("GROUND_BORDER");
                break;
            case DatAttrs.Attribute.ON_BOTTOM:
                sLogger.info("ON_BOTTOM");
                break;
            case DatAttrs.Attribute.ON_TOP:
                sLogger.info("ON_TOP");
                break;
            case DatAttrs.Attribute.NOT_WALKABLE:
                sLogger.info("NOT_WALKABLE");
                break;
            case DatAttrs.Attribute.NOT_MOVEABLE:
                sLogger.info("NOT_MOVEABLE");
                break;
            case DatAttrs.Attribute.HANGABLE:
                sLogger.info("HANGABLE");
                break;
            case DatAttrs.Attribute.BLOCK_PROJECTILE:
                sLogger.info("BLOCK_PROJECTILE");
                break;
            case DatAttrs.Attribute.LIGHT:
                int intensity = fileStream.getU16();
                int color = fileStream.getU16();
                sLogger.info("LIGHT, intensity {}, color {}", intensity, color);
                break;
            case DatAttrs.Attribute.FULL_GROUND:
                sLogger.info("FULL_GROUND");
                break;
            default:
                sLogger.info("Unrecognized attribute {}", String.format("0x%x", attribute));
                break;
        }
    }
}
