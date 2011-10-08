package jp.commun.minecraft.elchat.util;

import java.util.ArrayList;

public class StringUtils
{
	public static String join(ArrayList<String> list, String with)
	{
        StringBuffer sb = new StringBuffer();
        for (String str: list) {
            if (sb.length() > 0) {
                sb.append(with);
            }
            sb.append(str);
         }
         return sb.toString();
    }
	
	public static String convertHiraToKana(String text)
	{
	    StringBuffer sb = new StringBuffer(text);
	    for (int i = 0; i < sb.length(); i++) {
	    	char c = sb.charAt(i);
	    	if (c >= 'ぁ' && c <= 'ん') {
	    		sb.setCharAt(i, (char)(c - 'ぁ' + 'ァ'));
	    	}
	    }
	    return sb.toString();    
	}
}
