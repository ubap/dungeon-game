package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.dunegon.game.Game;
import com.mygdx.game.dunegon.game.Position;
import com.mygdx.game.dunegon.game.Thing;
import com.mygdx.game.dunegon.game.Tile;
import com.mygdx.game.dunegon.game.login.CharList;
import com.mygdx.game.dunegon.io.SpriteManager;
import com.mygdx.game.dunegon.io.ThingTypeManager;
import com.mygdx.game.dunegon.net.Protocol;
import com.mygdx.game.dunegon.net.ProtocolGame;
import com.mygdx.game.dunegon.net.ProtocolLogin;
import com.mygdx.game.graphics.Painter;
import com.mygdx.game.graphics.Point;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MyGdxGame extends ApplicationAdapter {
	private MainArguments arguments;

	private SpriteBatch batch;
	private static Tile[][] tiles;

	public MyGdxGame(MainArguments mainArguments) {
		this.arguments = mainArguments;
	}

	@Override
	public void create () {
		System.out.println("starting");

		tiles = new Tile[5][5];

		batch = new SpriteBatch();

		Painter.init(batch, 0, Gdx.graphics.getHeight());
		Game.init();
		ThingTypeManager.init();
		SpriteManager.init();

		try {

			URL sprUrl = new URL("file:" + arguments.getSprPath());
			SpriteManager.getInstance().loadSpr(sprUrl.toURI());
			URL datUrl = new URL("file:" + arguments.getDatPath());
			ThingTypeManager.getInstance().loadDat(datUrl.toURI());


			ProtocolLogin protocolLogin = new ProtocolLogin("1", "1");
			protocolLogin.connect(arguments.getGameAddress(), 7171);
			protocolLogin.waitForCharList();

			ProtocolGame protocolGame = new ProtocolGame("1", "1", protocolLogin.getCharList().getCharacters().get(0).getName());
			protocolGame.connect(arguments.getGameAddress(), 7172);

			Thread.sleep(2000);


			CountThread countThread = new CountThread();
			countThread.start();


		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		for (int x = -2; x < 3; x++) {
			for (int y = -2; y < 3; y++) {
				Tile tile = tiles[2 + x][2 + y];
				if (tile != null){
					tile.draw(new Point(100 + (x*32), 100 + (y*32)), 1, 0xFF);
				}
			}
		}

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	public class CountThread extends Thread {
		@Override
		public void run() {
			while (true) {

				Position position = Game.getInstance().getMap().getCentralPosition();

				for (int x = -2; x < 3; x++) {
					for (int y = -2; y < 3; y++) {
						Position tilePosition = new Position(position.getX() + x, position.getY() + y, position.getZ());
						tiles[2+x][2+y] = Game.getInstance().getMap().getTile(tilePosition);
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
}
