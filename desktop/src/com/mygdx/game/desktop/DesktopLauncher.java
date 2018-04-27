package com.mygdx.game.desktop;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MainArguments;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// packTextures();


		MainArguments mainArguments = new MainArguments();
		mainArguments.setDatPath("\\D:\\dev\\libgdx\\test-tibia-sprites\\core\\assets\\Tibia.dat");
		mainArguments.setSprPath("\\D:\\dev\\libgdx\\test-tibia-sprites\\core\\assets\\Tibia.spr");
		mainArguments.setGameAddress("127.0.0.1");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Title";
		config.height = 720;
		config.width = 1280;
		new LwjglApplication(new MyGdxGame(mainArguments), config);
	}

	public static void packTextures() {
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 8192;
		settings.maxHeight = 8192;
		settings.paddingX = 0;
		settings.paddingY = 0;
		settings.ignoreBlankImages = false;

		TexturePacker.process(settings, "sprites", "atlas", "pack");
	}
}
