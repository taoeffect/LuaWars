package rts.core.engine.layers.entities.effects;

import java.util.Random;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.layers.entities.others.Bird;
import rts.core.engine.layers.entities.others.Cloud;
import rts.utils.Timer;

public class EffectManager {

	private static final int TIME_BEFORE_DROP_EFFECT = 10000;
	private static final int TIME_BEFORE_SOUND_WIND_EFFECT = 120000;
	private static final Random RANDOM = new Random();

	private Engine engine;
	private Timer timer;
	private Timer soundTimer;

	public EffectManager(Engine engine) {
		this.engine = engine;
		this.timer = new Timer(TIME_BEFORE_DROP_EFFECT);
		this.soundTimer = new Timer(TIME_BEFORE_SOUND_WIND_EFFECT);
	}

	public void update(int delta) {
		timer.update(delta);
		soundTimer.update(delta);
		if (timer.isTimeComplete()) {
			if (RANDOM.nextInt(2) == 0) {
				engine.addEntity(new Cloud(engine));
			} else {
				engine.addEntity(new Bird(engine));
			}
			timer.resetTime();
		}
		if (soundTimer.isTimeComplete()) {
			GameSound.wind(RANDOM.nextInt(3) + 1);
			soundTimer.resetTime();
		}
	}

}
