package dunegon.game;

public class Game {
    private static Game INSTANCE;

    private LocalPlayer localPlayer;
    private Map map;

    private Game() {
        this.localPlayer = new LocalPlayer();
        this.map = new Map();
    }

    public static Game getInstance() {
        return INSTANCE;
    }

    /**
     * Has to be called before getInstance;
     */
    public static void init() {
        INSTANCE = new Game();
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public Map getMap() {
        return this.map;
    }
}
