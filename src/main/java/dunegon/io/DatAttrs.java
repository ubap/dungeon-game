package dunegon.io;

public class DatAttrs {

    public static final short THING_LAST_ATTR = 255;

    public static class Attribute {
        public static final short GROUND            = 0x00;
        public static final short GROUND_BORDER     = 0x01;
        public static final short ON_BOTTOM         = 0x02;
        public static final short ON_TOP            = 0x03;
        public static final short NOT_WALKABLE      = 0x0C;
        public static final short NOT_MOVEABLE      = 0x0D;
        public static final short BLOCK_PROJECTILE  = 0x0E;
        public static final short LIGHT             = 0x16;
        public static final short FULL_GROUND       = 0x1F;

        public static final short HANGABLE          = 0x12;

    }

    public static class ThingCategory {
        public static final short ThingCategoryItem = 0;
        public static final short ThingCategoryCreature = 1;
        public static final short ThingCategoryEffect = 2;
        public static final short ThingCategoryMissile = 3;
        public static final short ThingInvalidCategory = 4;
        public static final short ThingLastCategory = ThingInvalidCategory;
    }
}
