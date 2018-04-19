package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.dunegon.game.Game;
import com.mygdx.game.dunegon.game.login.CharList;
import com.mygdx.game.dunegon.io.ThingTypeManager;
import com.mygdx.game.dunegon.net.Protocol;
import com.mygdx.game.dunegon.net.ProtocolGame;
import com.mygdx.game.dunegon.net.ProtocolLogin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	private static Texture[] texture;

	private static int pos = 0;

	@Override
	public void create () {
		System.out.println("starting");

		Game.init();
		ThingTypeManager.init();
		SpriteManager.init();

		try {

			try {
				URL url = new URL("file:\\D:\\dev\\libgdx\\test-tibia-sprites\\core\\assets\\Tibia.spr");
				SpriteManager.getInstance().loadSpr(url.toURI());
				URL datUrl = new URL("file:\\D:\\dev\\libgdx\\test-tibia-sprites\\core\\assets\\Tibia.dat");
				ThingTypeManager.getInstance().loadDat(datUrl.toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			CharList charList = new CharList();

			Protocol protocol = new ProtocolLogin(charList, "1", "1");
			protocol.connect("127.0.0.1", 7171);

			synchronized (charList) {
				charList.wait();
			}

			ProtocolGame protocolGame = new ProtocolGame("1", "1", "Heh");
			protocolGame.connect("127.0.0.1", 7172);

			Thread.sleep(2000);


			batch = new SpriteBatch();


			texture = new Texture[100000];

			for (int i = 0; i < texture.length; i++) {
				texture[i] = SpriteManager.getInstance().getSpriteImage(i + 136);
			}

			SpriteManager.getInstance().unloadSpr();

			CountThread countThread = new CountThread();
			countThread.start();


		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (texture[pos] != null) {
			batch.draw(texture[pos], 50, 50);
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
				MyGdxGame.pos += 1;
				if (MyGdxGame.pos == MyGdxGame.texture.length) {
					MyGdxGame.pos = 0;
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
