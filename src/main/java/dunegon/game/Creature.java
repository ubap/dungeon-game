package dunegon.game;

import dunegon.io.DatAttrs;

public class Creature extends Thing {
    private int id;
    private String name;

    private double speedA;
    private double speedB;
    private double speedC;

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    protected ThingType getThingType() {
        return ThingTypeManager.getInstance().getThingType(id, DatAttrs.ThingCategory.ThingCategoryCreature);
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

}
