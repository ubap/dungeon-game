package dunegon.game;

public class Item extends Thing {

    public static Item create(int id) {
        Item val = new Item();
        val.setId(id);

        return val;
    }

    @Override
    public boolean isItem() {
        return true;
    }
}
