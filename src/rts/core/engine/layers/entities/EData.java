package rts.core.engine.layers.entities;

public final class EData {

	//EARTH ENTITIES
	
	public static final int MAX_EARTH_ENTITES = 17;
	
	public static final int MOVER_SOLDIER = 0;
	public static final int MOVER_SCOUT = 1;
	public static final int MOVER_JEEP = 2;
	public static final int MOVER_COLLECTOR = 3;
	public static final int MOVER_SCOUT_2 = 4;
	public static final int MOVER_TRANSPORT = 5;
	public static final int MOVER_ANTI_BUILDING = 6;
	public static final int MOVER_LIGHTING = 7;
	public static final int MOVER_ANTI_AERIAL = 8;
	public static final int MOVER_TANK = 9;
	public static final int MOVER_TANK_2 = 10;
	public static final int MOVER_FLAME_LAUNCHER = 11;
	public static final int MOVER_FLAME_LAUNCHER_2 = 12;
	public static final int MOVER_BUILDER = 13;
	public static final int MOVER_HACKER = 14;
	public static final int MOVER_ARTILLERY = 15;
	public static final int MOVER_ARTILLERY_2 = 16;
	
	// MARINE ENTITIES
	
	public static final int MAX_MARINE_ENTITES = 4;
	
	public static final int MOVER_DESTROYER  = 17;
	public static final int MOVER_MARINE_TRANSPORT = 18;
	public static final int MOVER_MISSILE_LAUNCHER = 19;
	public static final int MOVER_MARINE_SCOUT = 20;
	
	// SKY ENTITIES
	
	public static final int MOVER_HUNTER_1  = 21;
	public static final int MOVER_AIRSHIP = 22;
	public static final int MOVER_HUNTER_2 = 23;
	public static final int MOVER_SKY_SCOUT = 24;
	
	// BUILDING ENTITIES
	
	public static final int BUILDING_CONSTRUCTOR = 25;
	public static final int BUILDING_HEALER = 26;
	public static final int BUILDING_TELEVAT = 27;
	public static final int BUILDING_MISSILE_SILO = 28;
	public static final int BUILDING_ARTILLERY = 29;
	public static final int BUILDING_BIG_HEALER = 30;
	public static final int BUILDING_BARRACK = 31;
	public static final int BUILDING_DEV_CENTER = 32;
	public static final int BUILDING_REFINERY = 33;
	public static final int BUILDING_SPYRADAR = 34;
	public static final int BUILDING_STORAGE = 35;
	public static final int BUILDING_BUILDER = 36;
	public static final int BUILDING_RADAR = 37;
	public static final int BUILDING_STARPORT = 38;
	public static final int BUILDING_STARPORT_2 = 39;
	public static final int BUILDING_TURRET = 40;
	public static final int BUILDING_PORT = 41;
	public static final int BUILDING_LIGHTNING_WEAPON = 42;
	public static final int BUILDING_BIG_CONSTRUCTOR = 43;
	public static final int VERTICAL_BRIDGE = 44;
	public static final int HORIZONTAL_BRIDGE = 45;
	public static final int WALL = 46;
	
	// MISC ENTITES
	
	public static final int MINERAL = 47;
	public static final int OLD_CAR1 = 48;
	public static final int OLD_CAR2 = 49;
	public static final int OLD_BUILDING = 50;
	public static final int OLD_BARREL = 51;
	public static final int OLD_RADAR = 52;
	public static final int OLD_LAMP = 53;
	public static final int LAVA_EFFECT = 54;
	public static final int SWAMP_EFFECT = 55;
	
	// ACCESS METHODS
	
	public static boolean isEarthMover(int type){
		return type <= MOVER_ARTILLERY_2;
	}
	
	public static boolean isMarineMover(int type){
		return type > MOVER_ARTILLERY_2 && type <= MOVER_MARINE_SCOUT;
	}
	
	public static boolean isSkyMover(int type){
		return type > MOVER_MARINE_SCOUT && type <= MOVER_SKY_SCOUT;
	}
	
	public static boolean isBuilding(int type){
		return type > MOVER_SKY_SCOUT && type <= WALL;
	}
	
	public static boolean isMover(int type){
		return type >= 0 && type <= MOVER_SKY_SCOUT;
	}
	
	public static boolean isMisc(int type){
		return type >= MINERAL;
	}
	
	//Name
	public static final String[] NAMES = new String[]{
		"Soldier",
		"Scout",
		"Jeep",
		"Collector",
		"Scout V2",
		"Transport",
		"Eraser",
		"Lightning",
		"DCA",
		"Tank",
		"Tank V2",
		"Flame launcher",
		"Flame launcher V2",
		"Builder",
		"Hacker",
		"Artillery",
		"Artillery V2",
		"Destroyer",
		"Cargo",
		"Cruser",
		"Scout",
		"Hunter",
		"Airship",
		"Hunter V2",
		"Scout",
		"Constructor",
		"Healer",
		"Televat",
		"Missile silo",
		"Artillery",
		"Big healer",
		"Barrack",
		"Development center",
		"Refinery",
		"Spy radar",
		"Storage",
		"Builder",
		"Radar",
		"Starport",
		"Starport V2",
		"Turret",
		"Port",
		"Lightning cannon",
		"Big constructor",
		"Bridge",
		"Bridge",
		"Wall",
		"Mineral",
		"Old car",
		"Old car",
		"Old building",
		"Old barrel",
		"Old radar",
		"Old lamp"
	};
	
	//Building place (mover only)
	
	public static final int[][] BUILDING_PLACE = new int[][]{
		{EData.BUILDING_BARRACK},
		{EData.BUILDING_BARRACK},
		{EData.BUILDING_BARRACK},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{0},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{0},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{0},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{EData.BUILDING_CONSTRUCTOR,EData.BUILDING_BIG_CONSTRUCTOR},
		{0},
		
		{EData.BUILDING_PORT},
		{EData.BUILDING_PORT},
		{EData.BUILDING_PORT},
		{EData.BUILDING_PORT},
		
		{EData.BUILDING_STARPORT},
		{EData.BUILDING_STARPORT_2},
		{0},
		{EData.BUILDING_STARPORT},
	};
	
	
	// ENTITIES PRICE
	
	public static final int[] PRICE = new int[]{
		200,
		400,
		300,
		1800,
		0,
		800,
		1400,
		1000,
		800,
		700,
		0,
		1300,
		0,
		5000,
		1000,
		1500,
		0,
		
		400,
		800,
		2000,
		600,
		
		900,
		2200,
		0,
		600,
		
		//Buildings
		1200,
		300,
		1500,
		7500,
		800,
		1400,
		400,
		2500,
		2000,
		4000,
		200,
		8000,
		700,
		1000,
		1500,
		500,
		1000,
		5000,
		2000,
		0,
		0,
		100
	};
	
	// ENTITIES TEC LEVEL
	
	public static final int[] TEC_LEVEL = new int[]{
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_4,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_2,
		
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_2,
		
		//Buildings
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_4,
		ActiveEntity.TEC_LEVEL_4,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_1,
		//Builder
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_2,
		ActiveEntity.TEC_LEVEL_4,
		ActiveEntity.TEC_LEVEL_3,
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_1,
		ActiveEntity.TEC_LEVEL_1
	};
	
	// ENTITIES MAX LIFES
	
	public static final int[] MAX_LIFE = new int[]{
		// Earth Ents
		10,
		20,
		20,
		200,
		40,
		150,
		40,
		80,
		40,
		100,
		200,
		80,
		100,
		200,
		40,
		60,
		80,
		// Marine ents
		100,
		100,
		140,
		80,
		// Sky ents
		80,
		150,
		100,
		80,
		
		// Buildings ents
		80,
		60,
		210,
		210,
		160,
		200,
		100,
		200,
		200,
		100,
		80,
		300,
		150,
		100,
		100,
		100,
		200,
		250,
		200,
		0,
		0,
		100,
		
		//Misc
		200,
		50,
		50,
		100,
		50,
		100,
		50
	};
	
	// ENTITIES DEFAULT SPEED
	
	public static final float[] SPEED = new float[]{
		// Earth Ents
		0.03f,
		0.1f,
		0.1f,
		0.08f,
		0.12f,
		0.08f,
		0.06f,
		0.06f,
		0.06f,
		0.08f,
		0.08f,
		0.06f,
		0.06f,
		0.06f,
		0.08f,
		0.06f,
		0.06f,
		// Marine ents
		0.06f,
		0.08f,
		0.05f,
		0.1f,
		// Sky ents
		0.1f,
		0.05f,
		0.12f,
		0.14f,
		
	};
	
	// BULLET SPEED
	
	public static final float[] BULLET_SPEED = new float[]{
		// Earth Ents
		1f,
		0.5f,
		0.5f,
		0,
		0.4f,
		0,
		0.16f,
		0.2f,
		0.4f,
		0.4f,
		0.4f,
		0.2f,
		0.2f,
		0,
		0,
		0.16f,
		0.2f,
		// Marine ents
		0.2f,
		0,
		0.16f,
		0.24f,
		// Sky ents
		0.2f,
		0.16f,
		0.24f,
		0.2f
	};

	public static final float ARTILLERY_BULLET_SPEED = 0.6f;
	
	public static final float TURRET_BULLET_SPEED = 0.5f;
	
	public static final float LIGHTNING_MISSILE_SPEED = 0.5f;
	
	public static final float ABOMB_SPEED = 0.5f;
	
	// BULLET POWER 
	
	public static final int[][] BULLET_POWER = new int[][]{

		// Earth Ents        			                                   Marine        Sky             Buildings																Misc
		/* Soldier */ {3,1,1,1,1,1,2,2,2,1,1,1,1,1,3,3,2,                  1,1,1,1,      0,0,0,0,        1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,							0,2,2,2,2,2,2				},
		/* Scout   */ {4,3,2,2,1,2,4,3,2,1,1,1,1,1,4,4,3,                  2,2,2,2,      0,0,0,0,        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,							0,3,3,3,3,3,3				},
		/* Jeep    */ {5,4,2,2,2,2,5,5,3,1,1,1,1,1,4,4,3,                  2,2,2,2,      0,0,0,0,        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,							0,2,2,2,2,2,2				},
		/* Collect */ {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,                  0,0,0,0,      0,0,0,0,        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,							0,0,0,0,0,0,0				},
		/* Scout 2 */ {5,4,3,3,2,3,5,4,3,2,2,2,2,2,5,5,4,                  3,3,3,3,      4,4,4,4,        3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,							0,3,3,3,3,3,3				},
		/* Transpo */ {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,                  0,0,0,0,      0,0,0,0,        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,							0,0,0,0,0,0,0				},
		/* A build */ {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,                  1,1,1,1,      0,0,0,0,        45,45,45,45,45,45,45,45,45,45,45,45,45,45,45,45,45,45,45,45,45,45,		0,1,1,1,1,1,1				},
		/* Lightni */ {7,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,                  4,4,4,4,      0,0,0,0,        8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,							0,8,8,8,8,8,8				},
		/* A aeria */ {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,                  0,0,0,0,      15,15,15,15,    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,							0,0,0,0,0,0,0				},
		/* Tank    */ {1,10,10,10,10,10,10,10,10,10,10,10,10,10,15,20,20,  5,5,5,5,      0,0,0,0,        10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,		0,10,10,10,10,10,10			},
		/* Tank 2  */ {3,20,20,20,20,20,20,20,20,20,20,20,20,20,25,30,30,  8,8,8,8,      5,5,5,5,        15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,		0,15,15,15,15,15,15			},
		/* Flame   */ {12,12,12,8,12,8,15,15,15,8,6,10,10,10,20,20,20,     6,6,6,6,      0,0,0,0,        30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,		0,30,30,30,30,30,30			},
		/* Flame 2 */ {14,14,14,10,14,10,18,18,18,10,8,12,12,12,25,25,25,  8,8,8,8,      0,0,0,0,        40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,		0,40,40,40,40,40,40			},
		/* Builder */ {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,                  0,0,0,0,      0,0,0,0,        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,							0,0,0,0,0,0,0				},
		/* Hacker  */ {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,                  0,0,0,0,      0,0,0,0,        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,							0,0,0,0,0,0,0				},
		/* Artill  */ {10,8,8,6,8,6,10,12,12,10,10,12,12,10,12,14,14,      12,12,12,12,  6,6,6,6,        20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,		0,20,20,20,20,20,20			},
		/* Artill 2*/ {12,10,10,8,10,8,12,14,14,12,12,14,14,12,14,16,16,   14,14,14,14,  10,10,10,10,    25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,		0,25,25,25,25,25,25			},
		
		// Marine ents

		/* Destroy */ {10,6,8,6,8,6,10,10,10,10,10,12,12,10,12,14,14,      15,15,15,15,  7,7,7,7,        6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,							0,6,6,6,6,6,6				},
		/* M trans */ {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,                  0,0,0,0,      0,0,0,0,        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,							0,0,0,0,0,0,0				},
		/* Missi la*/ {20,20,20,20,20,20,20,20,20,10,10,20,20,20,20,20,20, 20,20,20,20,  0,0,0,0,        28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,		0,28,28,28,28,28,28			},
		/* M Scout */ {5,4,3,3,2,3,5,4,3,2,1,1,2,2,2,5,5,4,                5,5,5,5,      0,0,0,0,        8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,							0,8,8,8,8,8,8				},
		
		// Sky ents
		
		/* Hunter 1*/ {8,4,6,4,6,4,8,8,8,8,8,10,10,8,10,12,12,             6,6,6,6,      10,14,10,10,    6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,							0,6,6,6,6,6,6				},
		/* Airship */ {24,24,24,24,24,24,24,24,24,15,15,24,24,24,24,24,24, 24,24,24,24,  0,0,0,0,        30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,		0,30,30,30,30,30,30			},
		/* Hunter 2*/ {12,8,10,8,10,8,12,12,12,12,12,14,14,12,14,16,16,    10,10,10,10,  14,18,14,14,    10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,		0,10,10,10,10,10,10			},
		/* S Scout */ {5,4,3,3,2,3,5,4,3,2,1,1,2,2,2,5,5,4,                5,5,5,5,      8,12,8,8,       5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,							0,5,5,5,5,5,5				},
	};

	public static final int ARTILLERY_BULLET_POWER = 15;
	
	public static final int TURRET_BULLET_POWER  = 10;
	
	// VIEW
	
	public static final int[] VIEW = new int[]{
		// Earth
		6,
		6,
		6,
		6,
		6,
		8,
		6,
		6,
		6,
		6,
		6,
		4,
		4,
		6,
		6,
		8,
		8,
		// Marine
		6,
		6,
		6,
		8,
		// Fly
		6,
		8,
		8,
		8,
		// Buildings ents
		6,
		6,
		6,
		6,
		6,
		6,
		8,
		8,
		8,
		8,
		8,
		8,
		8,
		8,
		8,
		8,
		8,
		8,
		8,
		0,
		0,
		6
	};
	
	// SHOOT INTERVAL (time before next shoot in ms)
	
	public static final int[] SHOOT_INTERVAL = new int[]{
		// Earth
		800,
		800,
		800,
		0,
		750,
		0,
		1100,
		1000,
		900,
		1150,
		1000,
		1600,
		1500,
		0,
		0,
		1200,
		1100,
		// Marine
		900,
		0,
		1250,
		900,
		// Fly
		900,
		1250,
		850,
		800,
	};
	
	public static final int ARTILLERY_SHOOT_INTERVAL = 350;
	
	public static final int TURRET_SHOOT_INTERVAL = 400;
	
	// ASSOCIATED BULLET
	
	public static final int[] BULLET_TYPE = new int[]{
		// Earth
		0,
		0,
		0,
		0,
		3,
		0,
		5,
		6,
		9,
		10,
		17,
		0,
		0,
		0,
		0,
		4,
		5,
		// Marine
		3,
		0,
		5,
		0,
		// Fly
		18,
		5,
		19,
		16,
	};

	public static final int ARTILLERY_BULLET_TYPE = 5;
	
	public static final int TURRET_BULLET_TYPE = 7;

}
