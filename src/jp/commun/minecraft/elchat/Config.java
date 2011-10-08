package jp.commun.minecraft.elchat;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.util.config.Configuration;

public class Config
{
	protected ElChatPlugin plugin;
	protected Configuration config;
	protected File configFile;
	public Boolean romaToHiraEnabled = true;
	public ArrayList<String> ignoreWords = new ArrayList<String>(){{
		add("hi");
		add("tnt");
		add("wiki");
	}};
	public ArrayList<String> forceKana = new ArrayList<String>(){{
		add("べっど");
		add("ぶろっく");
		add("ぷらぐいん");
		add("どあ");
		add("ちぇすと");
		add("はっち");
		add("すぽーん");
		add("さいと");
		add("まっぷ");
		
	}};
	public String messageFormat = "<%prefix%player%suffix&f> %message";
	
	public Config(ElChatPlugin plugin)
	{
		this.plugin = plugin;
		
		this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
		this.plugin.getDataFolder().mkdirs();
		this.config = new Configuration(this.configFile);
		if (this.configFile.exists()) {
			this.load();
		} else {
			this.save();
		}
	}

	public void save()
	{
		this.config.setProperty("chat.message_format", this.messageFormat);
		this.config.setProperty("romatohira.enabled", this.romaToHiraEnabled);
		this.config.setProperty("romatohira.ignore_words", this.ignoreWords);
		this.config.setProperty("romatohira.force_kana", this.forceKana);
		this.config.save();
	}

	public void load()
	{
		if (!this.configFile.exists()) {
			this.save();
		}
		this.config.load();
		this.messageFormat = this.config.getString("chat.message_format");
		this.romaToHiraEnabled = this.config.getBoolean("romatohira.enabled", this.romaToHiraEnabled);
		this.ignoreWords = new ArrayList<String>(this.config.getStringList("romatohira.ignore_words", null));
		this.forceKana = new ArrayList<String>(this.config.getStringList("romatohira.force_kana", null));
	}

}
