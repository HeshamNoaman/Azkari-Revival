package com.buzzbox.mob.android.scheduler.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
public class StringUtils {
    public static Map<String, String> optionsInteger2map(String pairsSeparator, char keyValueSeparator, String options) {
        Map<String, String> mTemp = new LinkedHashMap<>();
        if (options != null) {
            StringTokenizer tok = new StringTokenizer(options, pairsSeparator);
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                int ix = token.indexOf(keyValueSeparator);
                if (ix == -1 || ix == token.length() - 1) {
                    mTemp.put(token, null);
                } else {
                    try {
                        String key = token.substring(0, ix);
                        String value = token.substring(ix + 1);
                        mTemp.put(key, value);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return mTemp;
    }
}
