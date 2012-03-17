package jp.commun.minecraft.elchat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/11
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class RomaToHiraData
{
    protected ElChatPlugin plugin;
    private List<String> ignoreWords;
    private List<String> kanaWords;
    private File ignoreWordsFile;
    private File kanaWordsFile;
    
    public RomaToHiraData(ElChatPlugin plugin)
    {
        this.plugin = plugin;
        ignoreWords = new ArrayList<String>();
        kanaWords = new ArrayList<String>();
        
        ignoreWordsFile = new File(plugin.getDataFolder(), "ignore_words.txt");
        kanaWordsFile = new File(plugin.getDataFolder(), "kana_words.txt");
    }

    public void loadConfig()
    {
        if (!ignoreWordsFile.exists()) {
            loadFromResource(ignoreWordsFile);
        }

        if (!kanaWordsFile.exists()) {
            loadFromResource(kanaWordsFile);
        }

        try {
            InputStream is = new FileInputStream(ignoreWordsFile);
            Scanner scanner = new Scanner(is);
            ignoreWords.clear();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#") || line.equals("")) continue;
                ignoreWords.add(line);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load file: " + ignoreWordsFile.getName() + " " + e.getMessage());
        }

        try {
            InputStream is = new FileInputStream(kanaWordsFile);
            Scanner scanner = new Scanner(is);
            kanaWords.clear();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#") || line.equals("")) continue;
                kanaWords.add(line);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load file: " + kanaWordsFile.getName() + " " + e.getMessage());
        }
    }
    
    public void reloadConfig()
    {
        loadConfig();
        saveConfig();
    }
    
    public void saveConfig()
    {
        
    }
    
    protected void loadFromResource(File file)
    {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = plugin.getClass().getResourceAsStream("/" + file.getName());
            os = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load file: " + file.getName());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not load file: " + file.getName());
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not load file: " + file.getName());
                }
            }
        }
    }

    public List<String> getIgnoreWords()
    {
        return ignoreWords;
    }

    public List<String> getKanaWords()
    {
        return kanaWords;
    }
}
