package jp.commun.minecraft.util.blocklogger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

public class HawkEyeLogger implements BlockLogger {
    private HawkEye plugin;
    public HawkEyeLogger(Plugin plugin) {
        if (!(plugin instanceof HawkEye)) throw new IllegalArgumentException("plugin is not HawkEye");
        this.plugin = (HawkEye)plugin;
    }

    @Override
    public void breakBlock(Player player, Block block) {
        DataEntry dataEntry;
        if (block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN)) {
            dataEntry = new SignEntry(player, DataType.SIGN_BREAK, block);
        } else {
            dataEntry = new BlockEntry(player, DataType.BLOCK_BREAK, block);
        }
        HawkEyeAPI.addEntry(plugin, dataEntry);
    }
}
