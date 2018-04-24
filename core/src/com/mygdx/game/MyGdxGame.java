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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;

	private static Tile[] northTiles;

	@Override
	public void create () {
		System.out.println("starting");

		northTiles = new Tile[3];

		batch = new SpriteBatch();
		Painter.init(batch);

		Game.init();
		ThingTypeManager.init();
		SpriteManager.init();

		try {

			URL url = new URL("file:\\D:\\dev\\libgdx\\test-tibia-sprites\\core\\assets\\Tibia.spr");
			SpriteManager.getInstance().loadSpr(url.toURI());
			URL datUrl = new URL("file:\\D:\\dev\\libgdx\\test-tibia-sprites\\core\\assets\\Tibia.dat");
			ThingTypeManager.getInstance().loadDat(datUrl.toURI());

			CharList charList = new CharList();

			Protocol protocol = new ProtocolLogin(charList, "1", "1");
			protocol.connect("127.0.0.1", 7171);

			synchronized (charList) {
				charList.wait();
			}

			ProtocolGame protocolGame = new ProtocolGame("1", "1", "Heh");
			protocolGame.connect("127.0.0.1", 7172);

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

//		if (northTiles[0] != null) {
//			northTiles[0].draw(new Point(100, 100), 1, 0xFF);
//		}
		if (northTiles[1] != null) {
			northTiles[1].draw(new Point(132, 100), 1, 0xFF);
		}
//		if (northTiles[2] != null) {
//			northTiles[2].draw(new Point(164, 100), 1, 0xFF);
//		}

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
				position = new Position(position.getX() - 1, position.getY() - 1, position.getZ());

				northTiles[0] = Game.getInstance().getMap().getTile(position);
				position = new Position(position.getX() + 1, position.getY() , position.getZ());
				northTiles[1] = Game.getInstance().getMap().getTile(position);

				position = new Position(position.getX() + 1, position.getY(), position.getZ());
				northTiles[2] = Game.getInstance().getMap().getTile(position);

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
