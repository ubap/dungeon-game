package com.mygdx.game.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

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

        spriteBatch.draw(texture, posX + dest.getLeft(), posY - dest.getBottom(), dest.getWidth(), dest.getHeight(),
                src.getLeft(), src.getTop(), src.getWidth(), src.getHeight(), false, false);
    }

    public void drawFrameBuffer(FrameBuffer frameBuffer) {
        spriteBatch.draw(frameBuffer.getColorBufferTexture(), 0, 300);
    }

    public void overwriteMask(Pixmap pixmap, Color maskedColor) {
        overwriteMask(pixmap, maskedColor, Color.WHITE, new Color(0, 0, 0, 1));
    }

    public void overwriteMask(Pixmap pixmap, Color maskedColor, Color insideColor, Color outsideColor) {
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                int pixelColor = pixmap.getPixel(x, y);

                int writeColor;
                if (pixelColor == maskedColor.toIntBits()) {
                    writeColor = insideColor.toIntBits();
                } else {
                    writeColor = outsideColor.toIntBits();
                }
                pixmap.drawPixel(y, x, writeColor);
            }
        }

    }
}
