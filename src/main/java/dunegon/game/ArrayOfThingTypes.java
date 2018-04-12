package dunegon.game;

import dunegon.io.DatAttrs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayOfThingTypes {
    private static ArrayOfThingTypes sArrayOfThingTypes;
    private static Logger sLogger = LoggerFactory.getLogger(ArrayOfThingTypes.class.getSimpleName());

    private ThingType[] mThingTypes;

    private ArrayOfThingTypes() {
        mThingTypes = new ThingType[DatAttrs.ThingCategory.ThingLastCategory];
        for (int i = 0; i < mThingTypes.length; i++) {
            mThingTypes[i] = new ThingType();
        }
    }

    /**
     * Thats some invalid singleton but it will help in thread safety.
     */
    public static void init() {
        sLogger.info("init");
        if (sArrayOfThingTypes == null) {
            sArrayOfThingTypes = new ArrayOfThingTypes();
        }
    }

    public static ArrayOfThingTypes getInstance() {
        return sArrayOfThingTypes;
    }

    public ThingType[] getThingTypesArray() {
        return mThingTypes;
    }

    public ThingType getThingType(int category) {
        return mThingTypes[category];
    }
}
