package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MainArguments;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		MainArguments mainArguments = new MainArguments();
		mainArguments.setDatPath("D:\\dev\\libgdx\\test-tibia-sprites\\core\\assets\\Tibia.dat");
		mainArguments.setSprPath("D:\\dev\\libgdx\\test-tibia-sprites\\core\\assets\\Tibia.spr");
		mainArguments.setGameAddress("127.0.0.1");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new MyGdxGame(mainArguments), config);
	}
}
