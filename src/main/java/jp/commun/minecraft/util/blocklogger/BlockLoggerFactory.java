package jp.commun.minecraft.util.blocklogger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class BlockLoggerFactory {
    public static BlockLogger getBlockLogger(Plugin plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        Plugin hawkEyePlugin = pm.getPlugin("HawkEye");
        if (hawkEyePlugin != null) {
            try {
                return new HawkEyeLogger(hawkEyePlugin);
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}
