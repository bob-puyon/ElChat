package jp.commun.minecraft.elchat.irc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ayu
 * Date: 12/03/11
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class IRCColor
{
    private static final Map<String, String> COLOR_MAP = new HashMap<String, String>() {{
        //put("00", "f"); // white
        put("01", "0"); // black
        put("02", "1"); // dark blue
        put("03", "2"); // dark green
        put("04", "4"); // red
        put("05", "8"); // brown
        put("06", "5"); // purple
        put("07", "6"); // olive
        put("08", "e"); // yellow
        put("09", "a"); // green
        put("10", "3"); // teal
        put("11", "b"); // cyan
        put("12", "9"); // blue
        put("13", "d"); // magenta
        put("14", "c"); // dark gray
        put("15", "7"); // light gray
    }};
    
    public static String toIRC(String text)
    {
        for (String ircColor: COLOR_MAP.keySet()) {
            text = text.replace("\u00A7" + COLOR_MAP.get(ircColor), "\u0003" + ircColor);
        }
        // white to normal
        text = text.replace("\u00A7f", "\u000f");
        return text;
    }
    
    public static String toGame(String text)
    {
        for (String ircColor: COLOR_MAP.keySet()) {
            text = text.replace("\u0003" + ircColor, "\u00A7" + COLOR_MAP.get(ircColor));
        }
        // normal to white
        text = text.replace("\u000f", "\u00A7f");
        return text;
    }
}
