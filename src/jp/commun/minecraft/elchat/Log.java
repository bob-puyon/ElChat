package jp.commun.minecraft.elchat;

import java.util.logging.Logger;

public class Log
{
    protected static Logger logger = Logger.getLogger("Minecraft");

    public static void die(String message)
    {
        logger.severe("[ElChat] " + message);
        ElChatPlugin.getPlugin().disablePlugin();
    }

    public static void severe(String message)
    {
        logger.severe("[ElChat] " + message);
    }

    public static void info(String message)
    {
        logger.info("[ElChat] " + message);
    }

}
