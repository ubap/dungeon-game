package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.dunegon.game.Game;
import com.mygdx.game.dunegon.game.MapView;
import com.mygdx.game.dunegon.game.Position;
import com.mygdx.game.dunegon.game.Thing;
import com.mygdx.game.dunegon.game.Tile;
import com.mygdx.game.dunegon.io.SpriteManager;
import com.mygdx.game.dunegon.io.ThingTypeManager;
import com.mygdx.game.dunegon.net.ProtocolGame;
import com.mygdx.game.dunegon.net.ProtocolLogin;
import com.mygdx.game.framework.EventDispatcher;
import com.mygdx.game.framework.FpsCounter;
import com.mygdx.game.graphics.Painter;
import com.mygdx.game.graphics.Point;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MyGdxGame extends ApplicationAdapter {
	private MainArguments arguments;

	private FpsCounter fpsCounter;
	private SpriteBatch batch;
	private BitmapFont font;
	private static Tile[][] tiles;


	private ProtocolGame protocolGame;

	public MyGdxGame(MainArguments mainArguments) {
		this.arguments = mainArguments;
	}

	@Override
	public void create () {
		System.out.println("starting");

		tiles = new Tile[5][5];

		batch = new SpriteBatch(8191);
		font = new BitmapFont();

		Painter.init(batch, 0, Gdx.graphics.getHeight());
		this.fpsCounter = new FpsCounter();

		EventDispatcher.init();
		ThingTypeManager.init();
		SpriteManager.init();
		MapView.init();

		DispatcherPoller dispatcherPoller = new DispatcherPoller();
		dispatcherPoller.start();

		try {

			URL sprUrl = new URL("file:" + arguments.getSprPath());
			SpriteManager.getInstance().loadSpr(sprUrl.toURI());
			URL datUrl = new URL("file:" + arguments.getDatPath());
			ThingTypeManager.getInstance().loadDat(datUrl.toURI());


			ProtocolLogin protocolLogin = new ProtocolLogin("1", "1");
			protocolLogin.connect(arguments.getGameAddress(), 7171);
			protocolLogin.waitForCharList();

			protocolGame = new ProtocolGame("1", "1", protocolLogin.getCharList().getCharacters().get(0).getName());
			Game.init(protocolGame);

			protocolGame.connect(arguments.getGameAddress(), 7172);

			Thread.sleep(2000);


            Gdx.input.setInputProcessor(new MyInputProcessor());

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

		protocolGame.lockReceiving();

		EventDispatcher.getInstance().poll();

		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		float scaleFactor = 1f;
        MapView.getInstance().draw(scaleFactor);

		this.fpsCounter.frame();
        String fpsString = String.format("FPS %.2f", this.fpsCounter.getFps());
		font.draw(batch, fpsString, 10, 20);

		String loadedSpritesCount = String.format("Loaded Sprites Count: %d", SpriteManager.getInstance().loadedSprites.size());
		font.draw(batch, loadedSpritesCount, 10, 50);

		batch.end();

		protocolGame.unlockReceiving();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	public class DispatcherPoller extends Thread {
		@Override
		public void run() {
//			while (true) {
//
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//					return;
//				}
//			}
		}
	}
}
