package dunegon.game;

public class Creature extends Thing {

    private double speedA;
    private double speedB;
    private double speedC;

    public void setSpeedFormula(double speedA, double speedB, double speedC) {
        this.speedA = speedA;
        this.speedB = speedB;
        this.speedC = speedC;
    }

    @Override
    public boolean isCreature() {
        return true;
    }
}
