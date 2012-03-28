package jp.commun.minecraft.elchat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExAdapter {
    private ElChatPlugin plugin;
    private boolean available = false;
    private PermissionManager permissionManager;

    public PermissionsExAdapter(ElChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void setPermissionsEx(Plugin plugin) {
        if (plugin != null) {
            plugin.getLogger().info("PermissionsEx detected! using: " + plugin.getDescription().getFullName());
            permissionManager = PermissionsEx.getPermissionManager();
            available = true;
        } else {
            permissionManager = null;
            available = false;
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
    
    public String getUserPrefix(Player player) {
        PermissionUser user = permissionManager.getUser(player);
        if (user != null) {
            return user.getPrefix();
        } else {
            return null;
        }
    }

    public String getUserSuffix(Player player) {
        PermissionUser user = permissionManager.getUser(player);
        if (user != null) {
            return user.getSuffix();
        } else {
            return null;
        }
    }
}
