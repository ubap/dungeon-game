package com.mygdx.game.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Painter {
    public static Painter INSTANCE;

    private SpriteBatch spriteBatch;

    public static void init(SpriteBatch spriteBatch) {
        INSTANCE = new Painter(spriteBatch);
    }

    public static Painter getInstance() {
        return INSTANCE;
    }

    private Painter(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    public void drawTexturedRect(Rect dest, Texture texture, Rect src) {

        spriteBatch.draw(texture, dest.getLeft(), 200 - dest.getBottom(), src.getLeft(), src.getTop(), src.getWidth(), src.getHeight());
    }

    public void drawTexturedRect(Rect dest, Texture texture) {
        spriteBatch.draw(texture, dest.getLeft(), dest.getBottom());
    }
}
