package rts.core.engine;

import java.util.Random;

import rts.utils.ResourceManager;
import rts.utils.Timer;

public class GameSound {

	private static final Random random = new Random();
	private static final Timer collectorAttackTimer = new Timer(10000);
	private static final Timer fundTimer = new Timer(30000);
	private static final Timer storageTimer = new Timer(30000);
	private static final Timer baseUnderAttackTimer = new Timer(10000);
	private static final Timer selectTimer = new Timer(1000);
	private static final Timer moveTimer = new Timer(1000);
	private static final Timer attackTimer = new Timer(1000);
	private static final Timer constructionEffectTimer = new Timer(1000);

	public static void init() {
		collectorAttackTimer.setTimeComplete();
		fundTimer.setTimeComplete();
		storageTimer.setTimeComplete();
		baseUnderAttackTimer.setTimeComplete();
		selectTimer.setTimeComplete();
		moveTimer.setTimeComplete();
		attackTimer.setTimeComplete();
		constructionEffectTimer.setTimeComplete();
	}

	public static void update(int delta) {
		collectorAttackTimer.update(delta);
		fundTimer.update(delta);
		storageTimer.update(delta);
		baseUnderAttackTimer.update(delta);
		selectTimer.update(delta);
		moveTimer.update(delta);
		attackTimer.update(delta);
		constructionEffectTimer.update(delta);
	}

	// OK
	public static void buildingReady() {
		ResourceManager.getSound("buildingReady").play();
	}

	public static void collectorAttack() {
		if (collectorAttackTimer.isTimeComplete()) {
			ResourceManager.getSound("aCollectorIsUnderAttack").play();
			collectorAttackTimer.resetTime();
		}
	}

	public static void flashWeapon() {
		ResourceManager.getSound("warningLightingWeaponDetected").play();
	}

	public static void nuclearMissile() {
		ResourceManager.getSound("warningNuclearMissileDetected").play();
	}

	// OK
	public static void insiffucientFunds() {
		if (fundTimer.isTimeComplete()) {
			ResourceManager.getSound("insiffucientFunds").play();
			fundTimer.resetTime();
		}
	}

	// OK
	public static void storageUnitNeeded() {
		if (storageTimer.isTimeComplete()) {
			ResourceManager.getSound("storageUnitNeeded").play();
			storageTimer.resetTime();
		}
	}

	// OK
	public static void unitReady() {
		ResourceManager.getSound("unitReady").play();
	}

	public static void ourBaseIsUnderAttack() {
		if (baseUnderAttackTimer.isTimeComplete()) {
			ResourceManager.getSound("ourBaseIsUnderAttack").play();
			baseUnderAttackTimer.resetTime();
		}
	}

	public static void shoot() {
		ResourceManager.getSound("shoot").play(1, 0.2f);
	}

	public static void explosion() {
		ResourceManager.getSound("explosion").play();
	}

	public static void fl() {
		ResourceManager.getSound("fl").play();
	}

	// Entities Sounds

	public static void selectMover() {
		if (selectTimer.isTimeComplete()) {
			if (random.nextInt(2) == 0)
				ResourceManager.getSound("sir").play();
			else
				ResourceManager.getSound("yesSir").play();
			selectTimer.resetTime();
		}
	}

	public static void moverMove() {
		if (moveTimer.isTimeComplete()) {
			switch (random.nextInt(3)) {
			case 0:
				ResourceManager.getSound("letsGo").play();
				break;
			case 1:
				ResourceManager.getSound("weMove").play();
				break;
			case 2:
				ResourceManager.getSound("itsOk").play();
				break;
			default:
				break;
			}
			moveTimer.resetTime();
		}
	}

	public static void moverAttack() {
		if (attackTimer.isTimeComplete()) {
			switch (random.nextInt(3)) {
			case 0:
				ResourceManager.getSound("itsOver").play();
				break;
			case 1:
				ResourceManager.getSound("iDestroy").play();
				break;
			case 2:
				ResourceManager.getSound("itsOk").play();
				break;
			default:
				break;
			}
			attackTimer.resetTime();
		}
	}

	public static void addMoney() {
		ResourceManager.getSound("money").play(1.0f, 0.2f);
	}

	public static void build() {
		ResourceManager.getSound("build").play(1.0f, 0.2f);
	}

	public static void wind(int number) {
		ResourceManager.getSound("wind" + number).play();
	}

	public static void buildingSold() {
		ResourceManager.getSound("buildingSold").play();
	}

	public static void repair() {
		ResourceManager.getSound("repair").play();
	}

	public static void construction() {
		ResourceManager.getSound("construction").play();
	}

	public static void constructionEffect() {
		if (constructionEffectTimer.isTimeComplete()) {
			ResourceManager.getSound("constructionEffect").play();
			constructionEffectTimer.resetTime();
		}
	}
}
