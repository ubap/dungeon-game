package dunegon.game.login;

import java.util.ArrayList;
import java.util.List;

public class CharList {
    private List<World> mWorldList;
    private List<Character> mCharacterList;
    private boolean mPremium;
    private long mPremDays;

    public CharList() {
        reset();
    }

    public void reset() {
        mWorldList = new ArrayList<>();
        mCharacterList = new ArrayList<>();
        mPremium = false;
        mPremDays = 0;
    }

    public void setWorldList(List<World> mWorldList) {
        this.mWorldList = mWorldList;
    }

    public void setCharacterList(List<Character> mCharacterList) {
        this.mCharacterList = mCharacterList;
    }

    public void setPremium(boolean mPremium) {
        this.mPremium = mPremium;
    }

    public void setPremDays(long mPremDays) {
        this.mPremDays = mPremDays;
    }
}
