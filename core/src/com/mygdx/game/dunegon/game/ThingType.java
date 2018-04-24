package com.mygdx.game.dunegon.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.dunegon.io.DatAttrs;
import com.mygdx.game.dunegon.io.SpriteManager;
import com.mygdx.game.graphics.Painter;
import com.mygdx.game.graphics.Point;
import com.mygdx.game.graphics.Rect;
import com.mygdx.game.graphics.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingType {
    private static Logger LOGGER = LoggerFactory.getLogger(ThingType.class.getSimpleName());

    private int mId;

    private int category;
    private boolean mGround;
    private int mGroundSpeed;

    private boolean mGroundBorder;
    private boolean mOnBottom;
    private boolean mOnTop;
    private boolean mContainer;
    private boolean mForceUse;
    private boolean mMultiUse;
    private boolean mNotWalkable;
    private boolean mNotMoveable;
    private boolean mNotPathable;
    private boolean mHangable;
    private boolean mBlockProjectile;
    private boolean mPickupable;
    private boolean mChargeable;
    private boolean mStackable;
    private boolean mLyingCorpse;
    private boolean mTranscluent;
    private boolean mFullGround;
    private boolean mLook;
    private boolean mFluidContainer;
    private boolean mHookSouth;
    private boolean mHookEast;
    private boolean mRotateable;
    private boolean mWrapable;
    private boolean mUnwrapable;
    private boolean mNoMoveAnim;
    private boolean mSplash;
    private boolean mDontHide;
    private boolean mAnimateAlways;
    private boolean mTopEffect;

    private boolean mHasDisplacement;
    private Point displacement;

    private boolean mHasLight;
    private int mLightIntensity;
    private int mLightColor;

    private boolean mHasMarket;
    private int mMarketCategory;
    private int mMarketTradeAs;
    private int mMarketShowAs;
    private String mMarketName;
    private int mMarketRestrictVocation;
    private int mMarketRestrictLevel;

    private boolean mHasElevation;
    private int mElevation;

    private boolean mHasMinimapColor;
    private int mMinimapColor;

    private boolean mHasCloth;
    private int mCloth;

    private boolean mHasUsable;
    private int mUsable;

    private boolean mHasLensHelp;
    private int mLensHelp;

    private boolean mHasWritable;
    private int mWritable;

    private boolean mHasWritableOnce;
    private int mWritableOnce;

    private int mAnimationPhases;

    private List<Integer> spriteIndexList;
    private Map<Integer, Texture> textures;
    private Size size;
    private int patternX;
    private int patternY;
    private int patternZ;
    private int layers;
    private Map<Integer, Map<Integer, Rect>> texturesFramesRect;
    private Map<Integer, Map<Integer, Point>> texturesFramesOffsets;


    public ThingType() {
        this.spriteIndexList = new ArrayList<Integer>();
        this.textures = new HashMap<Integer, Texture>();
        this.texturesFramesRect = new HashMap<Integer, Map<Integer, Rect>>();
        this.texturesFramesOffsets = new HashMap<Integer, Map<Integer, Point>>();
        this.displacement = new Point(0, 0);
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setGround(boolean ground, int groundSpeed) {
        this.mGround = ground;
        this.mGroundSpeed = groundSpeed;
    }

    public void setGroundBorder(boolean groundBorder) {
        this.mGroundBorder = groundBorder;
    }

    public void setOnBottom(boolean onBottom) {
        this.mOnBottom = onBottom;
    }

    public void setOnTop(boolean onTop) {
        this.mOnTop = onTop;
    }

    public void setContainer(boolean container) {
        this.mContainer = container;
    }

    public void setForceUse(boolean forceUse) {
        this.mForceUse = forceUse;
    }

    public void setMultiUse(boolean multiUse) {
        this.mMultiUse = multiUse;
    }

    public void setNotWalkable(boolean notWalkable) {
        this.mNotWalkable = notWalkable;
    }

    public void setNotMoveable(boolean notMoveable) {
        this.mNotMoveable = notMoveable;
    }

    public void setNotPathable(boolean notPathable) {
        this.mNotPathable = notPathable;
    }

    public void setHangable(boolean hangable) {
        this.mHangable = hangable;
    }

    public void setBlockProjectile(boolean blockProjectile) {
        this.mBlockProjectile = blockProjectile;
    }

    public void setPickupable(boolean pickupable) {
        this.mPickupable = pickupable;
    }

    public void setDisplacement(boolean displacement, int x, int y) {
        mHasDisplacement = displacement;
        this.displacement = new Point(x, y);
    }

    public void setLight(boolean light, int intensity, int color) {
        mHasLight = light;
        mLightIntensity = intensity;
        mLightColor = color;
    }

    public void setFullGround(boolean fullGround) {
        mFullGround = fullGround;
    }

    public void setMarket(boolean market, int category, int tradeAs, int showAs, String name,
                          int restrictVocation, int restrictLevel) {
        mHasMarket = market;
        mMarketCategory = category;
        mMarketTradeAs = tradeAs;
        mMarketShowAs = showAs;
        mMarketName = name;
        mMarketRestrictVocation = restrictVocation;
        mMarketRestrictLevel = restrictLevel;
    }

    public void setElevation(boolean hasElevation, int elevation) {
        mHasElevation = hasElevation;
        mElevation = elevation;
    }

    public void setMinimapColor(boolean hasMinimapColor, int minimapColor) {
        mHasMinimapColor = hasMinimapColor;
        mMinimapColor = minimapColor;
    }

    public void setChargeable(boolean chargeable) {
        mChargeable = chargeable;
    }

    public void setStackable(boolean stackable) {
        mStackable = stackable;
    }

    public void setCloth(boolean hasCloth, int cloth) {
        mHasCloth = hasCloth;
        mCloth = cloth;
    }

    public void setLyingCorpse(boolean lyingCorpse) {
        mLyingCorpse = lyingCorpse;
    }

    public void setTranscluent(boolean transcluent) {
        mTranscluent = transcluent;
    }

    public void setLook(boolean look) {
        mLook = look;
    }

    public void setFluidContainer(boolean fluidContainer) {
        mFluidContainer = fluidContainer;
    }

    public void setHookSouth(boolean hookSouth) {
        mHookSouth = hookSouth;
    }

    public void setHookEast(boolean hookEast) {
        this.mHookEast = hookEast;
    }

    public void setRotateable(boolean rotateable) {
        this.mRotateable = rotateable;
    }

    public void setWrapable(boolean wrapable) {
        this.mWrapable = wrapable;
    }

    public void setUnwrapable(boolean unwrapable) {
        this.mUnwrapable = unwrapable;
    }

    public void setNoMoveAnim(boolean noMoveAnim) {
        this.mNoMoveAnim = noMoveAnim;
    }

    public void setSplash(boolean splash) {
        this.mSplash = splash;
    }

    public void setDontHide(boolean dontHide) {
        this.mDontHide = dontHide;
    }

    public void setAnimateAlways(boolean animateAlways) {
        this.mAnimateAlways = animateAlways;
    }

    public void setTopEffect(boolean topEffect) {
        this.mTopEffect = topEffect;
    }

    public void setUsable(boolean hasUsable, int usable) {
        mHasUsable = hasUsable;
        mUsable = usable;
    }

    public void setLensHelp(boolean hasLensHelp, int lensHelp) {
        mHasLensHelp = hasLensHelp;
        mLensHelp = lensHelp;
    }

    public void setWritable(boolean hasWritable, int writable) {
        mHasWritable = hasWritable;
        mWritable = writable;
    }

    public void setWritableOnce(boolean hasWritableOnce, int writableOnce) {
        mHasWritableOnce = hasWritableOnce;
        mWritableOnce = writableOnce;
    }

    public void setAnimationPhases(int animationPhases) {
        mAnimationPhases = animationPhases;
    }
    public boolean isStackable() {
        return mStackable;
    }
    public boolean isFluidContainer() {
        return mFluidContainer;
    }
    public boolean isSplash() {
        return mSplash;
    }
    public boolean isChargeable() {
        return mChargeable;
    }
    public int getAnimationPhases() {
        return mAnimationPhases;
    }
    public boolean isGround() {
        return mGround;
    }
    public boolean isGroundBorder() {
        return mGroundBorder;
    }
    public boolean isOnBottom() {
        return mOnBottom;
    }
    public boolean isOnTop() {
        return mOnTop;
    }
    public boolean isTopEffect() {
        return mTopEffect;
    }
    public boolean isForceUse() {
        return mForceUse;
    }
    public boolean isIgnoreLook() {
        return mLook;
    }


    public void setSpriteIndexList(List<Integer> spriteIndexList) {
        this.spriteIndexList = spriteIndexList;
    }
    public List<Integer> getSpriteIndexList() {
        return spriteIndexList;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Size getSize() {
        return size;
    }

    public void setPatternX(int patternX) {
        this.patternX = patternX;
    }

    public void setPatternY(int patternY) {
        this.patternY = patternY;
    }

    public void setPatternZ(int patternZ) {
        this.patternZ = patternZ;
    }

    public int getPatternX() {
        return patternX;
    }
    public int getPatternY() {
        return patternY;
    }
    public int getPatternZ() {
        return patternZ;
    }
    public int getDrawElevation() {
        return mElevation;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }

    // todo
    public void draw(Point dest, float scaleFactor, int layer, int xPattern, int yPattern, int zPattern,
                     int animationPhase) {

        animationPhase = 0;

        if (animationPhase >= mAnimationPhases) {
            return;
        }



        Texture texture = getTexture(animationPhase);
        if (texture == null) {
            return;
        }

        int frameIndex = getTextureIndex(layer, xPattern, yPattern, zPattern);
        if (frameIndex >= texturesFramesRect.get(animationPhase).size()) {
            return;
        }

        Point textureOffset;
        Rect textureRect;

        textureOffset = texturesFramesOffsets.get(animationPhase).get(frameIndex);
        textureRect = texturesFramesRect.get(animationPhase).get(frameIndex);

        Rect screenRect = new Rect(dest.add(textureOffset.sub(displacement).sub(
                size.toPoint().sub(new Point(1,1 )).multiply(32) )),
                textureRect.getSize());

        Painter.getInstance().drawTexturedRect(screenRect, texture, textureRect);

    }

    // todo:
    public Texture getTexture(int animationPhase) {
        final int TILE_PIXELS = 32;

        if (textures.containsKey(animationPhase)) {
            return textures.get(animationPhase);
        }

        int textureLayers = 1;
        int numLayers = this.layers;
        if (category == DatAttrs.ThingCategory.ThingCategoryCreature && numLayers >= 2) {
            textureLayers = 5;
            numLayers = 5;
        }

        int indexSize = textureLayers * patternX * patternY * patternZ;
        Size textureSize = getBestTextureDimension(size.getWidth(), size.getHeight(), indexSize);

        Pixmap fullImage = new Pixmap(textureSize.getWidth() * TILE_PIXELS, textureSize.getHeight() * TILE_PIXELS, Pixmap.Format.RGBA8888);
        fullImage.fill();


        // todo: resize caches here
        if (!texturesFramesRect.containsKey(animationPhase)) {
            texturesFramesRect.put(animationPhase, new HashMap<Integer, Rect>());
        }
        if (!texturesFramesOffsets.containsKey(animationPhase)) {
            texturesFramesOffsets.put(animationPhase, new HashMap<Integer, Point>());
        }

        for (int z = 0; z < patternZ; z++) {
            for (int y = 0; y < patternY; y++) {
                for (int x = 0; x < patternX; x++) {
                    for (int l = 0; l < numLayers; l++) {
                        boolean spriteMask = (category == DatAttrs.ThingCategory.ThingCategoryCreature && l > 0);
                        int frameIndex = getTextureIndex(l % textureLayers, x, y, z);
                        Point framePos = new Point(frameIndex % (textureSize.getWidth() / size.getWidth()) * size.getWidth(),
                                frameIndex / (textureSize.getWidth() / size.getWidth()) * size.getHeight()).multiply(TILE_PIXELS);

                        for (int h = 0; h < size.getHeight(); h++) {
                            for (int w = 0; w < size.getWidth(); w++) {
                                int spriteIndex = getSpriteIndex(w, h, spriteMask ? 1 : l, x, y, z, animationPhase);
                                Pixmap spriteImage = SpriteManager.getInstance().getSpriteImage(spriteIndexList.get(spriteIndex));

                                if (spriteImage != null) {
                                    if (spriteMask) {
                                        // todo: mask;
                                    }
                                    Point spritePos = new Point(size.getWidth() - w - 1, size.getHeight() - h - 1).multiply(TILE_PIXELS);
                                    Point targetPos = framePos.add(spritePos);
                                    fullImage.drawPixmap(spriteImage, targetPos.getX(), targetPos.getY());
                                }
                            }
                        }

                        // todo: drawrect
                        Rect drawRect = new Rect(framePos.add(new Point(size.getWidth(), size.getHeight()).multiply(TILE_PIXELS)).add(new Point(-1, -1))
                                , framePos);

                        for (int xrect = framePos.getX(); xrect < framePos.getX() + size.getWidth() * TILE_PIXELS; xrect++) {
                            for (int yrect = framePos.getY(); yrect < framePos.getY() + size.getHeight() * TILE_PIXELS; yrect++) {
                                int pixel = fullImage.getPixel(xrect, yrect);
                                if ((pixel & 0x000000FF) != 0) {
                                    int top = Math.min(yrect, drawRect.getTop());
                                    int left = Math.min(xrect, drawRect.getLeft());
                                    int bottom = Math.max(yrect, drawRect.getBottom());
                                    int right = Math.max(xrect, drawRect.getRight());

                                    drawRect = drawRect.setTop(top).setLeft(left).setBottom(bottom).setRight(right);
                                }
                            }
                        }

                        texturesFramesRect.get(animationPhase).put(frameIndex, drawRect);
                        texturesFramesOffsets.get(animationPhase).put(frameIndex, drawRect.getTopLeft().sub(framePos));
                    }
                }
            }
        }

        Texture texture = new Texture(fullImage);
        textures.put(animationPhase, texture);

        return texture;
    }

    private Size getBestTextureDimension(int w, int h, int count) {
        final int MAX = 32;

        int k = 1;
        while (k < w) {
            k <<= 1;
        }
        w = k;

        k = 1;
        while (k < h) {
            k <<= 1;
        }
        h = k;

        int numSprites = w*h*count;
        if (numSprites > MAX*MAX || w > MAX || h > MAX) {
            throw new RuntimeException();
        }

        Size bestDimension = new Size(MAX, MAX);
        for (int i = w; i<=MAX; i<<=1) {
            for (int j = h; j <= MAX; j<<=1) {
                Size candidateDimension = new Size(i, j);
                if (candidateDimension.getArea() < numSprites) {
                    continue;
                }
                if (candidateDimension.getArea() < bestDimension.getArea() ||
                        (candidateDimension.getArea() == bestDimension.getArea()
                                && candidateDimension.getWidth() + candidateDimension.getHeight() < bestDimension.getWidth() + bestDimension.getHeight())) {
                    bestDimension = candidateDimension;
                }
            }
        }

        return bestDimension;
    }

    public int getTextureIndex(int l, int x, int y, int z) {
        return ((l * patternZ + z)
                * patternY + y)
                * patternX + x;
    }

    public int getSpriteIndex(int w, int h, int l, int x, int y, int z, int a) {
        int index =
                ((((((a % mAnimationPhases)
                        * patternZ + z)
                        * patternY + y)
                        * patternX + x)
                        * layers + l)
                        * size.getHeight() + h)
                        * size.getWidth() + w;
        if (index >= spriteIndexList.size()) {
            throw new RuntimeException();
        }
        return index;
    }
}
