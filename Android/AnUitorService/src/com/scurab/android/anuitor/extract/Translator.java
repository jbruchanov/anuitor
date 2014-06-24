package com.scurab.android.anuitor.extract;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        } else if (1 == visibility || View.INVISIBLE == visibility) { //1 by attrs
            return "Invisible";
        } else if (2 == visibility || View.GONE == visibility) {//2 by attrs
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
            return IdsHelper.getNameForId(value);
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

    public static Object choiceMode(int value) {
        switch (value) {
        case AbsListView.CHOICE_MODE_NONE:
            return "NONE";
        case AbsListView.CHOICE_MODE_SINGLE:
            return "SINGLE";
        case AbsListView.CHOICE_MODE_MULTIPLE:
            return "MULTIPLE";
        case AbsListView.CHOICE_MODE_MULTIPLE_MODAL:
            return "MUTLIPLE_MODAL";
        default:
            return String.format("UNKWNOWN (%s)", value);
        }
    }

    public static Object drawerLockMode(int value) {
        switch (value) {
        case DrawerLayout.LOCK_MODE_UNLOCKED:
            return "UNLOCKED";
        case DrawerLayout.LOCK_MODE_LOCKED_OPEN:
            return "OPEN";
        case DrawerLayout.LOCK_MODE_LOCKED_CLOSED:
            return "CLOSED";
        default:
            return String.format("UNKWNOWN (%s)", value);
        }
    }

    public static String stateListFlags(int[] states) {
        StringBuilder sb = new StringBuilder();
        for (int state : states) {
            String canname = IdsHelper.getNameForId(Math.abs(state));
            String[] names = canname.split("/");
            String name = names[names.length - 1];
            sb.append(name).append("=").append(state > 0).append(" ");
        }
        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - 1);
        } else {
            sb.append("default");
        }
        return sb.toString();
    }

    public static Object orientation(int value) {
        switch (value){
            case LinearLayout.VERTICAL:
                return "VERTICAL";
            case LinearLayout.HORIZONTAL:
                return "HORIZONTAL";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    public static Object scaleType(int value) {
        return ImageView.ScaleType.values()[value].toString();
    }

    public static Object textStyle(int value) {
        TextView tv;
        switch (value) {
            case Typeface.NORMAL:
                return "NORMAL";
            case Typeface.BOLD:
                return "BOLD";
            case Typeface.ITALIC:
                return "ITALIC";
            case Typeface.BOLD_ITALIC:
                return "BOLD_ITALIC";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    public static Object ellipsize(int value) {
        return TextUtils.TruncateAt.values()[value].toString();
    }

    public static Object shape(int value) {
        switch(value){
            case 0:
                return "rectangle";
            case 1:
                return "oval";
            case 2:
                return "line";
            case 3:
                return "ring";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    public static Object fragmentState(int value){
        switch (value){
            case -1:
                return "INVALID_STATE";
            case 0:
                return "INITIALIZING";
            case 1:
                return "CREATED";
            case 2:
                return "ACTIVITY_CREATED";
            case 3:
                return "STOPPED";
            case 4:
                return "STARTED";
            case 5:
                return "RESUMED";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }
}
