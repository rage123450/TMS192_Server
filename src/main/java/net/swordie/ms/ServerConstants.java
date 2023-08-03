package net.swordie.ms;

import net.swordie.ms.constants.JobConstants;

/**
 * Created on 2/18/2017.
 */
public class ServerConstants {
	public static final String DIR = System.getProperty("user.dir");
	public static final byte LOCALE = 6;
	public static final String WZ_DIR = DIR + "/WZ";
	public static final String DAT_DIR = DIR + "/dat";
	public static final int MAX_CHARACTERS = JobConstants.LoginJob.values().length * 3;
	public static final String SCRIPT_DIR = DIR + "/scripts";
	public static final String RESOURCES_DIR = DIR + "/resources";
	public static final String HANDLERS_DIR = DIR + "/src/main/java/net/swordie/ms/handlers";
	public static final short VERSION = 192;
	public static final String MINOR_VERSION = "2";
	public static final int LOGIN_PORT = 8484;
	public static final short CHAT_PORT = 8483;
	public static final int BCRYPT_ITERATIONS = 10;
	public static final long TOKEN_EXPIRY_TIME = 60 * 24; // minutes
}
