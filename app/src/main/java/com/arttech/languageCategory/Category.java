package com.arttech.languageCategory;

import android.content.Context;
import com.dot.constants.ApplicationConstants;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes.dex */
public class Category {
    static Hashtable<String, String> cat;

    private Category() {
    }

    private static void hashtableAdabter() {
        cat = new Hashtable<>();
        cat.put("2", "48");
        cat.put("1", "47");
        cat.put("5", "51");
        cat.put("6", "52");
    }

    public static String getLocalizedCategoryID(Context ctx) {
        hashtableAdabter();
        int id = ctx.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0).getInt(ApplicationConstants.LANGUAGE_PREFERENCES, -1);
        String catID = ctx.getSharedPreferences(ApplicationConstants.APPLICATION_PREFERENCES, 0).getString(ApplicationConstants.PRAYER_CAT_ID_NOTIFICATION_BAR, "-1");
        return id == 2 ? cat.get(catID) : catID;
    }
}
