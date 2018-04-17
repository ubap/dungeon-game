package dunegon.game;

import dunegon.io.DatAttrs;

public class Creature extends Thing {
    private long id;
    private String name;
    private Outfit outfit;

    private double speedA;
    private double speedB;
    private double speedC;

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    protected ThingType getThingType() {
        return ThingTypeManager.getInstance().getThingType(outfit.getId(), DatAttrs.ThingCategory.ThingCategoryCreature);
    }

    @Override
    public boolean isCreature() {
        return true;
    }

    public void setSpeedFormula(double speedA, double speedB, double speedC) {
        this.speedA = speedA;
        this.speedB = speedB;
        this.speedC = speedC;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOutfit(Outfit outfit) {
        this.outfit = outfit;
    }
}
