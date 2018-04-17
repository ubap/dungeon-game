package dunegon.game;

public class LocalPlayer extends Player {
    private double health;
    private double maxHealth;
    private double freeCapacity;
    private double totalCapacity;
    private double totalExperience;
    private double level;
    private double levelPercent;
    private double mana;
    private double maxMana;
    private double magicLevel;
    private double magicLevelPercent;
    private double baseMagicLevel;
    private double soul;
    private double stamina;
    private boolean known;
    private boolean pendingGame;


    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }


    public void setHealth(double health) {
        this.health = health;
    }
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }
    public void setFreeCapacity(double freeCapacity) {
        this.freeCapacity = freeCapacity;
    }
    public void setTotalCapacity(double totalCapacity) {
        this.totalCapacity = totalCapacity;
    }
    public void setTotalExperience(double totalExperience) {
        this.totalExperience = totalExperience;
    }
    public void setLevel(double level) {
        this.level = level;
    }
    public void setLevelPercent(double levelPercent) {
        this.levelPercent = levelPercent;
    }
    public void setMana(double mana) {
        this.mana = mana;
    }
    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }
    public void setMagicLevel(double magicLevel) {
        this.magicLevel = magicLevel;
    }
    public void setMagicLevelPercent(double magicLevelPercent) {
        this.magicLevelPercent = magicLevelPercent;
    }
    public void setBaseMagicLevel(double baseMagicLevel) {
        this.baseMagicLevel = baseMagicLevel;
    }
    public void setSoul(double soul) {
        this.soul = soul;
    }
    public void setStamina(double stamina) {
        this.stamina = stamina;
    }
    public void setKnown(boolean known) {
        this.known = known;
    }
    public void setPendingGame(boolean pendingGame) {
        this.pendingGame = pendingGame;
    }
}
