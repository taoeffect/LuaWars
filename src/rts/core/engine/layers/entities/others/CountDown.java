package rts.core.engine.layers.entities.others;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import rts.utils.Timer;

public class CountDown {

	private String info;
	private Color color;
	private Timer timeTimer;
	private Timer blinkTimer;
	private boolean blink;
	private int startTime;
	private int time;

	public CountDown(String info, Color color, int startTime) {
		this.info = info;
		this.color = color;
		this.startTime = startTime;
		this.time = startTime;
		this.timeTimer = new Timer(1000);
		this.blinkTimer = new Timer(500);
	}

	public void update(int delta) {
		timeTimer.update(delta);
		if (timeTimer.isTimeComplete() && time != 0) {
			time--;
			timeTimer.resetTime();
		}
	}
	
	public void updateBlink(int delta){
		if (isFinish()) {
			blinkTimer.update(delta);
			if (blinkTimer.isTimeComplete()) {
				blink = !blink;
				blinkTimer.resetTime();
			}
		}
	}

	public void render(Graphics g, int y) {
		g.setColor(color);
		if (!blink)
			g.drawString(info + ": " + (time / 60) + ":" + ((time % 60) < 10 ? "0" : "") + (time % 60), 5, y);
	}

	public boolean isFinish() {
		return time == 0;
	}

	public void reset() {
		time = startTime;
		blink = false;
		timeTimer.reset();
		blinkTimer.reset();
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}

}
