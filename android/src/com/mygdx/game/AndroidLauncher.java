package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MainArguments mainArguments = new MainArguments();
		mainArguments.setDatPath("/sdcard/Tibia.dat");
		mainArguments.setSprPath("/sdcard/Tibia.spr");
		mainArguments.setGameAddress("10.0.2.2");

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		initialize(new MyGdxGame(mainArguments), config);
	}
}
