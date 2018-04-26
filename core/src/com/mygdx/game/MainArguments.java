package com.mygdx.game;

public abstract  class MainArguments {
    private String sprPath;
    private String datPath;
    private String gameAddress;

    public MainArguments setSprPath(String sprPath) {
        this.sprPath = sprPath;
        return this;
    }

    public MainArguments setDatPath(String datPath) {
        this.datPath = datPath;
        return this;
    }

    public MainArguments setGameAddress(String gameAddress) {
        this.gameAddress = gameAddress;
        return this;
    }

    public String getSprPath() {
        return sprPath;
    }

    public String getDatPath() {
        return datPath;
    }

    public String getGameAddress() {
        return gameAddress;
    }

    public abstract void ensureResourceFiles();
}
