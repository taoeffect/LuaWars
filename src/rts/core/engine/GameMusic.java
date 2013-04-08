package rts.core.engine;

import org.luawars.Log;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;

import rts.utils.ResourceManager;

public class GameMusic {

	private static Music mainTheme;

	public static void initMainTheme() {
		try {
			//mainTheme = new Music("resources/others/main_theme.ogg", true);
			mainTheme = new Music("resources/others/main_theme.ogg");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public static void initMusics() {
        Log.logEnterMethod(Log.DEBUG);
        ResourceManager.getMusic("music_1").addListener(new GameMusicListener(2));
		ResourceManager.getMusic("music_2").addListener(new GameMusicListener(3));
		ResourceManager.getMusic("music_3").addListener(new GameMusicListener(1));
        Log.logExitMethod(Log.DEBUG);
	}

	public static void loopMainTheme() {
		mainTheme.loop();
	}

	public static void playMusic() {
		mainTheme.stop();
		ResourceManager.getMusic("music_1").play();
	}

	public static void stopMusic() {
		ResourceManager.getMusic("music_1").stop();
		ResourceManager.getMusic("music_2").stop();
		ResourceManager.getMusic("music_3").stop();
	}

	private static class GameMusicListener implements MusicListener {

		private int nextMusic;

		public GameMusicListener(int nextMusic) {
			this.nextMusic = nextMusic;
		}

		@Override
		public void musicEnded(Music arg0) {
			ResourceManager.getMusic("music_" + nextMusic).play();
		}

		@Override
		public void musicSwapped(Music arg0, Music arg1) {

		}
	}
}
