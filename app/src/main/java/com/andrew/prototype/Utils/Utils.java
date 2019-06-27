package com.andrew.prototype.Utils;

import android.content.Context;

import java.text.DecimalFormat;

public class Utils {

    public static String priceFormat(long totalPrice) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(totalPrice);
    }

    public int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
