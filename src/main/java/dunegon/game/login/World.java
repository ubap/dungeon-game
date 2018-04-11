package dunegon.game.login;

public class World {
    private int mId;
    private String mName;
    private String mIp;
    private int mPort;

    public World(int id, String name, String ip, int port) {
        mId = id;
        mName = name;
        mIp = ip;
        mPort = port;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getIp() {
        return mIp;
    }

    public int getPort() {
        return mPort;
    }
}
