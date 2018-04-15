package dunegon.game;

public class LocalPlayer extends Player{

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }
}
