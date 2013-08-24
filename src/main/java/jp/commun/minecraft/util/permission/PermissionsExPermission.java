/*
 * Copyright 2012 ayunyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.commun.minecraft.util.permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExPermission implements Permission {
    private PermissionManager permissionManager;

    public PermissionsExPermission(Plugin plugin) {
        this.permissionManager = PermissionsEx.getPermissionManager();
    }

    public String getGroup(Player player) {
        PermissionUser user = permissionManager.getUser(player);
        if (user != null) {
            String[] groups = user.getGroupsNames();
            if (groups.length > 0) return groups[0];
        }
        return null;
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
