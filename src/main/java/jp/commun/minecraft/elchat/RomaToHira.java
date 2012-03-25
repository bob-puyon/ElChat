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

import jp.commun.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RomaToHira {
    private static HashMap<String, String> romaTable = new HashMap<String, String>() {{
        put("a", "あ");
        put("i", "い");
        put("yi", "い");
        put("u", "う");
        put("wu", "う");
        put("whu", "う");
        put("e", "え");
        put("o", "お");
        put("la", "ぁ");
        put("xa", "ぁ");
        put("li", "ぃ");
        put("xi", "ぃ");
        put("lyi", "ぃ");
        put("xyi", "ぃ");
        put("lu", "ぅ");
        put("xu", "ぅ");
        put("le", "ぇ");
        put("xe", "ぇ");
        put("lye", "ぇ");
        put("xye", "ぇ");
        put("lo", "ぉ");
        put("xo", "ぉ");
        put("wha", "うぁ");
        put("whi", "うぃ");
        put("wi", "うぃ");
        put("whe", "うぇ");
        put("we", "うぇ");
        put("who", "うぉ");
        put("ka", "か");
        put("ca", "か");
        put("ki", "き");
        put("ku", "く");
        put("cu", "く");
        put("qu", "く");
        put("ke", "け");
        put("ko", "こ");
        put("co", "こ");
        put("lka", "ヵ");
        put("xka", "ヵ");
        put("lke", "ヶ");
        put("xke", "ヶ");
        put("ga", "が");
        put("gi", "ぎ");
        put("gu", "ぐ");
        put("ge", "げ");
        put("go", "ご");
        put("kya", "きゃ");
        put("kyi", "きぃ");
        put("kyu", "きゅ");
        put("kye", "きぇ");
        put("kyo", "きょ");
        put("qya", "くゃ");
        put("qyu", "くゅ");
        put("qwa", "くぁ");
        put("qa", "くぁ");
        put("kwa", "くぁ");
        put("qwi", "くぃ");
        put("qi", "くぃ");
        put("qyi", "くぃ");
        put("qwu", "くぅ");
        put("qwe", "くぇ");
        put("qe", "くぇ");
        put("qye", "くぇ");
        put("qwo", "くぉ");
        put("qo", "くぉ");
        put("gya", "ぎゃ");
        put("gyi", "ぎぃ");
        put("gyu", "ぎゅ");
        put("gye", "ぎぇ");
        put("gyo", "ぎょ");
        put("gwa", "ぐぁ");
        put("gwi", "ぐぃ");
        put("gwu", "ぐぅ");
        put("gwe", "ぐぇ");
        put("gwo", "ぐぉ");
        put("sa", "さ");
        put("si", "し");
        put("ci", "し");
        put("shi", "し");
        put("su", "す");
        put("se", "せ");
        put("ce", "せ");
        put("so", "そ");
        put("za", "ざ");
        put("zi", "じ");
        put("ji", "じ");
        put("zu", "ず");
        put("ze", "ぜ");
        put("zo", "ぞ");
        put("sya", "しゃ");
        put("sha", "しゃ");
        put("syi", "しぃ");
        put("syu", "しゅ");
        put("shu", "しゅ");
        put("sye", "しぇ");
        put("she", "しぇ");
        put("syo", "しょ");
        put("sho", "しょ");
        put("swa", "すぁ");
        put("swi", "すぃ");
        put("swu", "すぅ");
        put("swe", "すぇ");
        put("swo", "すぉ");
        put("zya", "じゃ");
        put("ja", "じゃ");
        put("jya", "じゃ");
        put("zyi", "じぃ");
        put("jyi", "じぃ");
        put("zyu", "じゅ");
        put("ju", "じゅ");
        put("jyu", "じゅ");
        put("zye", "じぇ");
        put("je", "じぇ");
        put("jye", "じぇ");
        put("zyo", "じょ");
        put("jo", "じょ");
        put("jyo", "じょ");
        put("ta", "た");
        put("ti", "ち");
        put("chi", "ち");
        put("tu", "つ");
        put("tsu", "つ");
        put("te", "て");
        put("to", "と");
        put("ltu", "っ");
        put("xtu", "っ");
        put("ltsu", "っ");
        put("da", "だ");
        put("di", "ぢ");
        put("du", "づ");
        put("de", "で");
        put("do", "ど");
        put("tya", "ちゃ");
        put("cha", "ちゃ");
        put("cya", "ちゃ");
        put("tyi", "ちぃ");
        put("cyi", "ちぃ");
        put("tyu", "ちゅ");
        put("chu", "ちゅ");
        put("cyu", "ちゅ");
        put("tye", "ちぇ");
        put("che", "ちぇ");
        put("cye", "ちぇ");
        put("tyo", "ちょ");
        put("cho", "ちょ");
        put("cyo", "ちょ");
        put("tsa", "つぁ");
        put("tsi", "つぃ");
        put("tse", "つぇ");
        put("tso", "つぉ");
        put("tha", "てゃ");
        put("thi", "てぃ");
        put("thu", "てゅ");
        put("the", "てぇ");
        put("tho", "てょ");
        put("twa", "とぁ");
        put("twi", "とぃ");
        put("twu", "とぅ");
        put("twe", "とぇ");
        put("two", "とぉ");
        put("dya", "ぢゃ");
        put("dyi", "ぢぃ");
        put("dyu", "ぢゅ");
        put("dye", "ぢぇ");
        put("dyo", "ぢょ");
        put("dha", "でゃ");
        put("dhi", "でぃ");
        put("dhu", "でゅ");
        put("dhe", "でぇ");
        put("dho", "でょ");
        put("dwa", "どぁ");
        put("dwi", "どぃ");
        put("dwu", "どぅ");
        put("dwe", "どぇ");
        put("dwo", "どぉ");
        put("na", "な");
        put("ni", "に");
        put("nu", "ぬ");
        put("ne", "ね");
        put("no", "の");
        put("nya", "にゃ");
        put("nyi", "にぃ");
        put("nyu", "にゅ");
        put("nye", "にぇ");
        put("nyo", "にょ");
        put("nna", "んな");
        put("nni", "んに");
        put("nno", "んの");
        put("ha", "は");
        put("hi", "ひ");
        put("hu", "ふ");
        put("fu", "ふ");
        put("he", "へ");
        put("ho", "ほ");
        put("ba", "ば");
        put("bi", "び");
        put("bu", "ぶ");
        put("be", "べ");
        put("bo", "ぼ");
        put("pa", "ぱ");
        put("pi", "ぴ");
        put("pu", "ぷ");
        put("pe", "ぺ");
        put("po", "ぽ");
        put("hya", "ひゃ");
        put("hyi", "ひぃ");
        put("hyu", "ひゅ");
        put("hye", "ひぇ");
        put("hyo", "ひょ");
        put("fya", "ふゃ");
        put("fyu", "ふゅ");
        put("fyo", "ふょ");
        put("fwa", "ふぁ");
        put("fa", "ふぁ");
        put("fwi", "ふぃ");
        put("fi", "ふぃ");
        put("fyi", "ふぃ");
        put("fwu", "ふぅ");
        put("fwe", "ふぇ");
        put("fe", "ふぇ");
        put("fye", "ふぇ");
        put("fwo", "ふぉ");
        put("fo", "ふぉ");
        put("bya", "びゃ");
        put("byi", "びぃ");
        put("byu", "びゅ");
        put("bye", "びぇ");
        put("byo", "びょ");
        put("va", "ヴぁ");
        put("vi", "ヴぃ");
        put("vu", "ヴ");
        put("ve", "ヴぇ");
        put("vo", "ヴぉ");
        put("vya", "ヴゃ");
        put("vyi", "ヴぃ");
        put("vyu", "ヴゅ");
        put("vye", "ヴぇ");
        put("vyo", "ヴょ");
        put("pya", "ぴゃ");
        put("pyi", "ぴぃ");
        put("pyu", "ぴゅ");
        put("pye", "ぴぇ");
        put("pyo", "ぴょ");
        put("ma", "ま");
        put("mi", "み");
        put("mu", "む");
        put("me", "め");
        put("mo", "も");
        put("mya", "みゃ");
        put("myi", "みぃ");
        put("myu", "みゅ");
        put("mye", "みぇ");
        put("myo", "みょ");
        put("ya", "や");
        put("yu", "ゆ");
        put("yo", "よ");
        put("lya", "ゃ");
        put("xya", "ゃ");
        put("lyu", "ゅ");
        put("xyu", "ゅ");
        put("lyo", "ょ");
        put("xyo", "ょ");
        put("ra", "ら");
        put("ri", "り");
        put("ru", "る");
        put("re", "れ");
        put("ro", "ろ");
        put("rya", "りゃ");
        put("ryi", "りぃ");
        put("ryu", "りゅ");
        put("rye", "りぇ");
        put("ryo", "りょ");
        put("wa", "わ");
        put("wo", "を");
        put("n", "ん");
        put("nn", "ん");
        put("xn", "ん");
        put("lwa", "ゎ");
        put("xwa", "ゎ");
        put(".", "。");
        put(",", "、");
        put("bb", "っ");
        put("cc", "っ");
        put("dd", "っ");
        put("ff", "っ");
        put("gg", "っ");
        put("hh", "っ");
        put("jj", "っ");
        put("kki", "っき");
        put("kku", "っく");
        put("kko", "っこ");
        put("kkyo", "っきょ");
        put("ll", "っ");
        put("mm", "っ");
        put("ppi", "っぴ");
        put("ppu", "っぷ");
        put("ppo", "っぽ");
        put("qq", "っ");
        put("rr", "っ");
        put("ss", "っ");
        put("ssha", "っしゃ");
        put("tta", "った");
        put("tti", "っち");
        put("ttu", "っつ");
        put("tte", "って");
        put("tto", "っと");
        put("vv", "っ");
        put("ww", "っ");
        put("xx", "っ");
        put("yy", "っ");
        put("zz", "っ");
        put("-", "ー");
    }};

    public static boolean hasHiragana(String text) {
        return text.matches(".*[あ-んア-ン]+.*");
    }

    public static String convert(String text) {
        List<String> ignoreWords = ElChatPlugin.getPlugin().getRomaToHiraData().getIgnoreWords();
        List<String> kanaWords = ElChatPlugin.getPlugin().getRomaToHiraData().getKanaWords();

        String[] words = text.trim().split("[\\s,\\.]+");

        Boolean converted = false;
        ArrayList<String> convertedWords = new ArrayList<String>();

        for (String word1 : words) {
            String word = word1.replaceAll("w+", "w");
            if (ignoreWords != null && ignoreWords.contains(word.toLowerCase())) {
                convertedWords.add(word);
                continue;
            }

            StringBuilder sb = new StringBuilder();
            int wordLen = word.length();
            for (int j = 0; j < wordLen; j++) {
                Boolean match = false;

                for (int k = 4; k > 0; k--) {
                    if (wordLen < j + k) continue;

                    String s = word.substring(j, j + k).toLowerCase();

                    if (romaTable.containsKey(s)) {
                        String h = romaTable.get(s);
                        sb.append(h);
                        if (!h.equals("っ")) {
                            j += k - 1;
                        }
                        match = converted = true;
                        break;
                    }
                }

                if (!match && wordLen > j) sb.append(word.substring(j, j + 1));


            }

            String convertedWord = sb.toString();
            if (kanaWords.contains(convertedWord)) {
                convertedWords.add(StringUtils.convertHiraToKana(convertedWord));
            } else {
                convertedWords.add(convertedWord);
            }
        }

        if (converted) {
            return StringUtils.join(convertedWords, " ");
        } else {
            return text;
        }
    }
}
