package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.mygdx.game.dunegon.game.Consts;
import com.mygdx.game.dunegon.game.Game;

public class MyInputProcessor implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) {
            Game.getInstance().forceWalk(Consts.Direction.NORTH);
            return true;
        }
        if (keycode == Input.Keys.DOWN) {
            Game.getInstance().forceWalk(Consts.Direction.SOUTH);
            return true;
        }
        if (keycode == Input.Keys.LEFT) {
            Game.getInstance().forceWalk(Consts.Direction.WEST);
            return true;
        }
        if (keycode == Input.Keys.RIGHT) {
            Game.getInstance().forceWalk(Consts.Direction.EAST);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
