package rts.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

/**
 * This class is link to the configuration file and provide static
 * getters/setters to use the current game configuration.
 * 
 * @author Vincent PIRAULT
 * 
 */
public class Configuration {

	private static String fileLocation;
	private static Properties configurationFile;

	/**
	 * Initialize the configuration file with the given path.
	 * 
	 * @param fileLocation
	 *            The path of the file.
	 * @throws IOException
	 *             If the file can't be loaded.
	 */
	public static void init(String fileLocation) throws IOException {
		Configuration.fileLocation = fileLocation;
		updateConfigFile();
	}

	/**
	 * Update the current configuration settings.
	 * 
	 * @throws IOException
	 *             If the file can't be loaded.
	 */
	public static void updateConfigFile() throws IOException {
		InputStreamReader is = new InputStreamReader(new FileInputStream(fileLocation));
		configurationFile = new Properties();
		configurationFile.load(is);
		is.close();
	}

	/**
	 * Save the current set configuration to the configuration file.
	 * 
	 * @throws IOException
	 */
	public static void saveNewConfig() throws IOException {
		OutputStream os = new FileOutputStream(fileLocation);
		configurationFile.store(os, "");
		os.flush();
		os.close();
		updateConfigFile();
	}

	// Getters and Setters

	/**
	 * Get the game frame width.
	 */
	public static int getWidth() {
		return Integer.parseInt(configurationFile.getProperty("width", "800"));
	}

	/**
	 * Set the game frame width.
	 * 
	 * @param width
	 *            The new width.
	 */
	public static void setWidth(int width) {
		configurationFile.setProperty("width", width + "");
	}

	/**
	 * Get the game frame height.
	 */
	public static int getHeight() {
		return Integer.parseInt(configurationFile.getProperty("height", "600"));
	}

	/**
	 * Set the game frame height.
	 * 
	 * @param height
	 *            The new height.
	 */
	public static void setHeight(int height) {
		configurationFile.setProperty("height", height + "");
	}

	/**
	 * Check if the full screen is on.
	 * 
	 * @return true is the frame is in full screen, false otherwise.
	 */
	public static boolean isFullScreen() {
		return configurationFile.getProperty("fullscreen").equals("true");
	}

	/**
	 * Set the on/off full screen for game frame.
	 * 
	 * @param fullscreen
	 *            true to put the full screen on, false to put it off.
	 */
	public static void setFullScreen(boolean fullscreen) {
		configurationFile.setProperty("fullscreen", (fullscreen) ? "true" : "false");
	}

	/**
	 * Get the game target FPS.
	 */
	public static int getTargetFPS() {
		return Integer.parseInt(configurationFile.getProperty("targetFps", "100"));
	}

	/**
	 * Set the game target FPS.
	 * 
	 * @param fps
	 *            The new target fps.
	 */
	public static void setTargetFPS(int fps) {
		configurationFile.setProperty("targetFps", fps + "");
	}

	/**
	 * Check if smooth deltas is on.
	 * 
	 * @return true is smooth deltas is on, false otherwise.
	 */
	public static boolean isSmoothDeltas() {
		return configurationFile.getProperty("smoothDeltas").equals("true");
	}

	/**
	 * Set the on/off smooth deltas.
	 * 
	 * @param smoothDeltas
	 *            true to put smooth deltas on, false to put it off.
	 */
	public static void setSmoothDeltas(boolean smoothDeltas) {
		configurationFile.setProperty("smoothDeltas", (smoothDeltas) ? "true" : "false");
	}

	/**
	 * Check if VSynch is on.
	 * 
	 * @return true is VSynch is on, false otherwise.
	 */
	public static boolean isVSynch() {
		return configurationFile.getProperty("vsynch").equals("true");
	}

	/**
	 * Set the on/off VSynch.
	 * 
	 * @param vsynch
	 *            true to put VSynch on, false to put it off.
	 */
	public static void setVSynch(boolean vsynch) {
		configurationFile.setProperty("vsynch", (vsynch) ? "true" : "false");
	}

	/**
	 * Get the current music volume.
	 * 
	 * @return the current music volume (0 - 1).
	 */
	public static float getMusicVolume() {
		return Float.parseFloat(configurationFile.getProperty("musicVol", "1"));
	}

	/**
	 * Set the music volume.
	 * 
	 * @param volume
	 *            the new music volume, must be between 0 and 1.
	 */
	public static void setMusicVolume(float volume) {
		configurationFile.setProperty("musicVol", volume + "");
	}

	/**
	 * Get the current sound volume.
	 * 
	 * @return the current sound volume (0 - 1).
	 */
	public static float getSoundVolume() {
		return Float.parseFloat(configurationFile.getProperty("soundVol", "1"));
	}

	/**
	 * Set the sound volume.
	 * 
	 * @param volume
	 *            the new sound volume, must be between 0 and 1.
	 */
	public static void setSoundVolume(float volume) {
		configurationFile.setProperty("soundVol", volume + "");
	}

	/**
	 * Get the player pseudo, this value is Player by default.
	 * 
	 * @return the player pseudo.
	 */
	public static String getPseudo() {
		String pseudo = configurationFile.getProperty("pseudo", "Player");
		// -- pseudo or AI pseudo is forbidden
		if (pseudo.equals("--"))
			return "fb ps";
		if (pseudo.equals("AI"))
			return "fb ps";
		return configurationFile.getProperty("pseudo", "Player");
	}

	/**
	 * Set the player pseudo.
	 * 
	 * @param pseudo
	 *            The new player pseudo.
	 */
	public static void setPeudo(String pseudo) {
		configurationFile.setProperty("pseudo", pseudo);
	}

	/**
	 * Check if debug is on.
	 * 
	 * @return true is debug is on, false otherwise.
	 */
	public static boolean isDebug() {
		return configurationFile.getProperty("debug").equals("true");
	}

	/**
	 * Set the on/off debug.
	 * 
	 * @param debug
	 *            true to put debug on, false to put it off.
	 */
	public static void setDebug(boolean debug) {
		configurationFile.setProperty("debug", (debug) ? "true" : "false");
	}

	/**
	 * Get the current TCP port, 9990 by default.
	 * 
	 * @return the current TCP port.
	 */
	public static int getTcpPort() {
		return Integer.parseInt(configurationFile.getProperty("tcpPort", 9990 + ""));
	}

	/**
	 * Set the current TCP port.
	 * 
	 * @param port
	 *            The new TCP port.
	 */
	public static void setTcpPort(int port) {
		configurationFile.setProperty("tcpPort", port + "");
	}

	/**
	 * Get the current UDP port, 9990 by default.
	 * 
	 * @return the current UDP port.
	 */
	public static int getUdpPort() {
		return Integer.parseInt(configurationFile.getProperty("udpPort", 9990 + ""));
	}

	/**
	 * Set the current UDP port.
	 * 
	 * @param port
	 *            The new UDP port.
	 */
	public static void setUdpPort(int port) {
		configurationFile.setProperty("udpPort", port + "");
	}

	public static int getUdpListeningServerPort() {
		return Integer.parseInt(configurationFile.getProperty("udpListeningServerPort", 9991 + ""));
	}

	public static void setUdpListeningServerPort(int port) {
		configurationFile.setProperty("udpListeningServerPort", port + "");
	}

	public static int getUdpListeningClientPort() {
		return Integer.parseInt(configurationFile.getProperty("udpListeningClientPort", 9991 + ""));
	}

	public static void setUdpListeningClientPort(int port) {
		configurationFile.setProperty("udpListeningClientPort", port + "");
	}

    /**
     * Get the current Profile.
     *
     *
     */
    public static String getProfile() {
        return configurationFile.getProperty("profile", "Default");
    }

    /**
     * Set the current Profile.
     *
     *  HAS TO BE THE NAME OF AN EXISTING PROFILE
     *
     */
    public static void setProfile(String profile) {
        configurationFile.setProperty("profile", profile + "");
    }

    /**
     * Get the current profile's Progress.
     */
    public static int getProgress(String profile) {
        return Integer.parseInt(configurationFile.getProperty(profile, "1"));
    }

    /**
     * Set the current profile's Progress.
     *
     * CANNOT BE LARGER THAN THE AMOUNT OF MAPS IN THE GAME
     *
     */
    public static void setProgress(String profile, int progress) {
        configurationFile.setProperty(profile, progress + "");
    }

}
