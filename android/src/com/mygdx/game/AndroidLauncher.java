package com.mygdx.game;

import android.os.Bundle;
import android.os.Environment;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

import java.io.File;
import java.io.IOException;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MainArguments mainArguments = new MainArguments() {
			@Override
			public void ensureResourceFiles() {
				File externalStorageDirectory = Environment.getExternalStorageDirectory();

				try {
					File newFile = new File(externalStorageDirectory, "plik.txt");
					newFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return;
			}
		};

		mainArguments.setDatPath("/storage/sdcard0/Tibia.dat");
		mainArguments.setSprPath("/storage/sdcard0/Tibia.spr");
		mainArguments.setGameAddress("192.168.0.199");

//		mainArguments.setDatPath("/storage/emulated/0/Tibia.dat");
//		mainArguments.setSprPath("/storage/emulated/0/Tibia.spr");
//		mainArguments.setGameAddress("192.168.0.199");

//		mainArguments.setDatPath("/sdcard/Tibia.dat");
//		mainArguments.setSprPath("/sdcard/Tibia.spr");
//		mainArguments.setGameAddress("10.0.2.2");

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		initialize(new MyGdxGame(mainArguments), config);
	}
}
