package com.mygdx.game.dunegon.game;

public class Outfit {
    int id;
    int auxId;
    int head;
    int body;
    int legs;
    int feet;
    int addons;
    int mount;

    int thingCategory;

    public void setId(int id) {
        this.id = id;
    }

    public void setAuxId(int auxId) {
        this.auxId = auxId;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }

    public void setFeet(int feet) {
        this.feet = feet;
    }

    public void setAddons(int addons) {
        this.addons = addons;
    }

    public void setMount(int mount) {
        this.mount = mount;
    }

    public void setThingCategory(int thingCategory) {
        this.thingCategory = thingCategory;
    }

    public int getId() {
        return id;
    }

    public int getThingCategory() {
        return thingCategory;
    }
}
