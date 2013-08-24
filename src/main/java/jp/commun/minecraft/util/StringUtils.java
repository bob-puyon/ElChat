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
package jp.commun.minecraft.util;

import java.util.List;

public class StringUtils
{
    public static String join(String[] array, String separator)
    {
        StringBuffer sb = new StringBuffer();
        for (String str: array) {
            if (sb.length() > 0) sb.append(separator);
            sb.append(str);
        }
        return sb.toString();
    }

    public static String join(List<String> list, String separator)
    {
        StringBuffer sb = new StringBuffer();
        for (String str: list) {
            if (sb.length() > 0) sb.append(separator);
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

    public static String joinedArgs(String[] args, int initial)
    {
        StringBuffer sb = new StringBuffer();
        if (args.length <= initial) initial = 0;
        for (int i = initial; i < args.length; i++) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(args[i]);
        }
        return sb.toString();
    }
}
