package com.scurab.android.anuitor.extract;

import android.view.Gravity;
import android.view.View;

/**
 *
 * Created by jbruchanov on 21/05/2014.
 */
public class Translator {

    /**
     * Translate view visibility to readable value
     * @param visibility
     * @return
     */
    public static String visibility(int visibility) {
        if (View.VISIBLE == visibility) {
            return "Visible";
        } else if (View.INVISIBLE == visibility) {
            return "Invisible";
        } else if (View.GONE == visibility) {
            return "Gone";
        } else {
            return String.format("Unknown value:'%s'", visibility);
        }
    }

    /**
     * Translate int value of gravity to human readable value
     * @param gravity
     * @return
     */
    public static String gravity(int gravity) {
        StringBuilder sb = new StringBuilder();
        if ((gravity & Gravity.CENTER) == Gravity.CENTER) {
            sb.append("Center");
        } else {
            if ((gravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
                sb.append("CenterVertical|");
            }
            if ((gravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
                sb.append("CenterHorizontal|");
            }
            if ((gravity & Gravity.TOP) == Gravity.TOP) {
                sb.append("Top|");
            }
            if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
                sb.append("Left|");
            }
            if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
                sb.append("Right|");
            }
            if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                sb.append("Bottom|");
            }
            int len = sb.length();
            if (len > 0) {
                sb.setLength(len - 1);
            }
        }
        return sb.toString();
    }
}
