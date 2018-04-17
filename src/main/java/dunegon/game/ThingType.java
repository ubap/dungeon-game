package dunegon.game;

public class ThingType {
    private int mId;

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
    private int mDisplacementX;
    private int mDisplacementY;

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

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
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
        mDisplacementX = x;
        mDisplacementY = y;
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
}
