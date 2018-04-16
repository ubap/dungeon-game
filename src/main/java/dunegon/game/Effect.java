package dunegon.game;

public class Effect extends Thing {

    public static Effect create(int id) {
        Effect val = new Effect();
        val.setId(id);
        return val;
    }

    @Override
    public boolean isEffect() {
        return true;
    }
}
