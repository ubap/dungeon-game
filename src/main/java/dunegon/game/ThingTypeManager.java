package dunegon.game;

import dunegon.io.DatAttrs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThingTypeManager {
    private static ThingTypeManager sThingTypeManager;
    private static Logger sLogger = LoggerFactory.getLogger(ThingTypeManager.class.getSimpleName());

    private ThingType[] mThingTypes;

    private ThingTypeManager() {
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
        if (sThingTypeManager == null) {
            sThingTypeManager = new ThingTypeManager();
        }
    }

    public static ThingTypeManager getInstance() {
        return sThingTypeManager;
    }

    public ThingType[] getThingTypesArray() {
        return mThingTypes;
    }

    public ThingType getThingType(int category) {
        return mThingTypes[category];
    }
}
