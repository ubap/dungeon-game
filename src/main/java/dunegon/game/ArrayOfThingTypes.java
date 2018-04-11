package dunegon.game;

import dunegon.io.DatAttrs;

public class ArrayOfThingTypes {
    private ThingType[] mThingTypes;

    public ArrayOfThingTypes() {
        mThingTypes = new ThingType[DatAttrs.ThingCategory.ThingLastCategory];
        for (int i = 0; i < mThingTypes.length; i++) {
            mThingTypes[i] = new ThingType();
        }
    }

    public ThingType[] getThingTypesArray() {
        return mThingTypes;
    }
}
