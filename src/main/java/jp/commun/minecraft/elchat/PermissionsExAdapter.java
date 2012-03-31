package jp.commun.minecraft.elchat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExAdapter extends PluginAdapter {
    private boolean available = false;
    private PermissionManager permissionManager;

    public PermissionsExAdapter(ElChatPlugin plugin) {
        super(plugin);
    }

    @Override
    public void setPlugin(Plugin permissionsExPlugin) {
        if (permissionsExPlugin != null) {
            this.plugin.getLogger().info("PermissionsEx detected. using: " + permissionsExPlugin.getDescription().getFullName());
            permissionManager = PermissionsEx.getPermissionManager();
            available = true;
        } else {
            permissionManager = null;
            available = false;
        }
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
    
    public String getPlayerPrefix(Player player) {
        PermissionUser user = permissionManager.getUser(player);
        if (user != null) {
            return user.getPrefix();
        } else {
            return null;
        }
    }

    public String getPlayerSuffix(Player player) {
        PermissionUser user = permissionManager.getUser(player);
        if (user != null) {
            return user.getSuffix();
        } else {
            return null;
        }
    }
}
