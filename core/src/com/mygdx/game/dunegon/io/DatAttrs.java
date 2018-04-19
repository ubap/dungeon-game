package com.mygdx.game.dunegon.io;

public class DatAttrs {



    public static class Attribute {
        public static final short GROUND            = 0x00;
        public static final short GROUND_BORDER     = 0x01;
        public static final short ON_BOTTOM         = 0x02;
        public static final short ON_TOP            = 0x03;
        public static final short CONTAINER         = 0x04;
        public static final short STACKABLE         = 0x05;
        public static final short FORCE_USE         = 0x06;
        public static final short MULTI_USE         = 0x07;
        public static final short WRITABLE          = 0x08;
        public static final short WRITABLE_ONCE     = 0x09;
        public static final short FLUID_CONTAINER   = 0x0A;
        public static final short SPLASH            = 0x0B;
        public static final short NOT_WALKABLE      = 0x0C;
        public static final short NOT_MOVEABLE      = 0x0D;
        public static final short BLOCK_PROJECTILE  = 0x0E;
        public static final short NOT_PATHABLE      = 0x0F;
        public static final short NO_MOVE_ANIM      = 0x10;
        public static final short PICKUPABLE        = 0x11;
        public static final short HANGABLE          = 0x12;
        public static final short HOOK_SOUTH        = 0x13;
        public static final short HOOK_EAST         = 0x14;
        public static final short ROTATEABLE        = 0x15;
        public static final short LIGHT             = 0x16;
        public static final short DONT_HIDE         = 0x17;
        public static final short TRANSCLUENT       = 0x18;
        public static final short DISPLACEMENT      = 0x19;
        public static final short ELEVATION         = 0x1A;
        public static final short LYING_CORPSE      = 0x1B;
        public static final short ANIMATE_ALWAYS    = 0x1C;
        public static final short MINIMAP_COLOR     = 0x1D;
        public static final short LENS_HELP         = 0x1E;
        public static final short FULL_GROUND       = 0x1F;
        public static final short LOOK              = 0x20;
        public static final short CLOTH             = 0x21;
        public static final short MARKET            = 0x22;
        public static final short USABLE            = 0x23;
        public static final short WRAPABLE          = 0x24;
        public static final short UNWRAPABLE        = 0x25;
        public static final short TOP_EFFECT        = 0x26;

        public static final short CHARGABLE         = 0xFE; // deprecated

        public static final short LAST_ATTR         = 0xFF;
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
