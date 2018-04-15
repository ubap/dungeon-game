package dunegon.game;

public class Game {
    private static Game INSTANCE;

    private LocalPlayer localPlayer;

    private Game() {
        localPlayer = new LocalPlayer();
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
}
