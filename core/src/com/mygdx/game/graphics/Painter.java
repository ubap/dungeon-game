package com.mygdx.game.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Y raises from top to bottom.
 * X raises from left to right.
 */
public class Painter {
    public static Painter INSTANCE;

    private SpriteBatch spriteBatch;
    private int posX;
    private int posY;

    public static void init(SpriteBatch spriteBatch, int posX, int posY) {
        INSTANCE = new Painter(spriteBatch);
        INSTANCE.posX = posX;
        INSTANCE.posY = posY;
    }

    public static Painter getInstance() {
        return INSTANCE;
    }

    private Painter(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    public void drawTexturedRect(Rect dest, Texture texture, Rect src) {

        spriteBatch.draw(texture, posX + dest.getLeft(), posY - dest.getBottom(), src.getLeft(), src.getTop(), src.getWidth(), src.getHeight());
    }
}
