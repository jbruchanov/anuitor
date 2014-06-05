package com.scurab.android.anuitor.extract;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.InputType;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.lang.reflect.Field;

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

    /**
     * Translate value from layout params
     * @param value
     * @return
     */
    public static Object layoutSize(int value) {
        if (ViewGroup.LayoutParams.MATCH_PARENT == value) {
            return "match_parent";
        } else if (ViewGroup.LayoutParams.WRAP_CONTENT == value) {
            return "wrap_content";
        } else {
            return value;
        }
    }

    private static final String[] RELATIVE_LAYOUT_RULES =
            new String[]{"leftOf", "rightOf", "above", "below", "alignBaseline", "alignLeft", "alignTop", "alignRight", "alignBottom", "alignParentLeft", "alignParentTop", "alignParentRight", "alignParentBottom", "center", "centerHorizontal", "centerVertical"};

    public static String relativeLayoutParamRuleName(int index){
        return "layoutParams_" + (index < RELATIVE_LAYOUT_RULES.length ? RELATIVE_LAYOUT_RULES[index] : index);
    }

    public static Object relativeLayoutParamRuleValue(int value) {
        if (RelativeLayout.TRUE == value) {
            return true;
        } else if (value == 0) {
            return "false/NO_ID";
        } else {
            return IdsHelper.getValueForId(value);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static String importantForA11Y(int value){
        switch (value){
            case View.IMPORTANT_FOR_ACCESSIBILITY_YES:
                return "YES";
            case View.IMPORTANT_FOR_ACCESSIBILITY_NO:
                return "NO";
            case View.IMPORTANT_FOR_ACCESSIBILITY_AUTO:
                return "AUTO";
            case View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS:
                return "NO_HIDE_DESCENDANTS";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    public static Object layerType(int value) {
        switch (value){
            case View.LAYER_TYPE_HARDWARE:
                return "HW";
            case View.LAYER_TYPE_SOFTWARE:
                return "SW";
            case View.LAYER_TYPE_NONE:
                return "None";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    public static Object layoutDirection(int value) {
        switch (value) {
            case View.LAYOUT_DIRECTION_INHERIT:
                return "Inherit";
            case View.LAYOUT_DIRECTION_LOCALE:
                return "Locale";
            case View.LAYOUT_DIRECTION_LTR:
                return "LTR";
            case View.LAYOUT_DIRECTION_RTL:
                return "RTL";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    private static final int[] LINK_MASKS =       new int[]{Linkify.EMAIL_ADDRESSES, Linkify.MAP_ADDRESSES, Linkify.PHONE_NUMBERS, Linkify.WEB_URLS};
    private static final String[] LINK_MASKS_VALUES = new String[]{"EMAIL_ADDRESSES",       "MAP_ADDRESSES",       "PHONE_NUMBERS",      "WEB_URLS"};

    public static Object linkMask(int autoLinkMask) {
        if (Linkify.ALL == autoLinkMask) {
            return "ALL";
        } else if (autoLinkMask == 0) {
            return "NONE";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < LINK_MASKS.length; i++) {
                if ((autoLinkMask & LINK_MASKS[i]) == LINK_MASKS[i]) {
                    sb.append(LINK_MASKS_VALUES[i]).append("|");
                }
            }
            int length = sb.length();
            if (length > 0) {
                sb.setLength(length - 1);
            }
            return sb.toString();
        }
    }

    public static Object inputType(int inputType) {
        if (inputType == InputType.TYPE_NUMBER_VARIATION_NORMAL) {//0
            return "TYPE_NUMBER_VARIATION_NORMAL";
        }
        StringBuilder sb = new StringBuilder();
        Field[] fields = InputType.class.getFields();
        try {
            for (Field field : fields) {
                if (field.isAccessible() && field.getType() == int.class) {
                    String name = field.getName();
                    int value = field.getInt(null);//static
                    if ((inputType & value) == value) {
                        sb.append(name).append("|");
                    }
                }
            }
            int length = sb.length();
            if (length > 0) {
                sb.setLength(length - 1);
            }
        } catch (IllegalAccessException e) {
            sb.setLength(0);
            sb.append(e.getMessage());
            e.printStackTrace();
        }
        return sb.toString();
    }
}
