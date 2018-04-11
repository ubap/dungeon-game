package dunegon.game.login;

public class Character {
    private int mWorldId;
    private String mName;

    public Character(int worldId, String name) {
        mWorldId = worldId;
        mName = name;
    }

    public int getWorldId() {
        return mWorldId;
    }

    public String getName() {
        return mName;
    }
}
