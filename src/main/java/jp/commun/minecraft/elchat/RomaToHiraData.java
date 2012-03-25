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

package jp.commun.minecraft.elchat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RomaToHiraData
{
    protected ElChatPlugin plugin;
    private List<String> ignoreWords;
    private List<String> kanaWords;
    private Map<String, String> kanjiWords;
    private File ignoreWordsFile;
    private File kanaWordsFile;
    private File kanjiWordsFile;
    
    public RomaToHiraData(ElChatPlugin plugin)
    {
        this.plugin = plugin;
        ignoreWords = new ArrayList<String>();
        kanaWords = new ArrayList<String>();
        
        ignoreWordsFile = new File(plugin.getDataFolder(), "ignore_words.txt");
        kanaWordsFile = new File(plugin.getDataFolder(), "kana_words.txt");
        kanjiWordsFile = new File(plugin.getDataFolder(), "kanji_words.txt");
    }

    public void loadConfig()
    {
        if (!ignoreWordsFile.exists()) {
            loadFromResource(ignoreWordsFile);
        }

        if (!kanaWordsFile.exists()) {
            loadFromResource(kanaWordsFile);
        }

        if (!kanjiWordsFile.exists()) {
            loadFromResource(kanjiWordsFile);
        }

        // 無視単語リストをロード
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

        // カタカナリストをロード
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

        // 漢字テーブルをロード
        try {
            InputStream is = new FileInputStream(kanjiWordsFile);
            Scanner scanner = new Scanner(is);
            kanjiWords.clear();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#") || line.equals("")) continue;
                String[] words = line.split("\\s+", 2);
                if (words.length != 2) continue;
                kanjiWords.put(words[0], words[1]);
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

    public Map<String, String> getKanjiWords() {
        return kanjiWords;
    }
}
