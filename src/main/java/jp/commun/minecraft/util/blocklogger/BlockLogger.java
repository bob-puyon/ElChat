package jp.commun.minecraft.util.blocklogger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BlockLogger {
    public void breakBlock(Player player, Block block);
}
