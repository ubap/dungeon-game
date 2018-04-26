package com.mygdx.game.graphics;

import com.badlogic.gdx.graphics.Texture;

public class Sprite {
    private Texture texture;
    private Rect rect;


    public Sprite(Texture texture, Rect rect) {
        this.texture = texture;
        this.rect = rect;
    }
}
