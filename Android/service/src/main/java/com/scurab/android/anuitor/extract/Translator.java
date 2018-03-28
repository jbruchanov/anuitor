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
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.lang.reflect.Field;

/**
 * Created by jbruchanov on 21/05/2014.
 */
public class Translator {

    /**
     * Translate view visibility to readable value
     *
     * @param visibility
     * @return
     */
    public String visibility(int visibility) {
        if (View.VISIBLE == visibility) {
            return "VISIBLE";
        } else if (1 == visibility || View.INVISIBLE == visibility) { //1 by attrs
            return "INVISIBLE";
        } else if (2 == visibility || View.GONE == visibility) {//2 by attrs
            return "GONE";
        } else {
            return String.format("Unknown value:'%s'", visibility);
        }
    }

    /**
     * Translate int value of gravity to human readable value
     *
     * @param gravity
     * @return
     */
    public String gravity(int gravity) {
        StringBuilder sb = new StringBuilder();
        if ((gravity & Gravity.CENTER) == Gravity.CENTER) {
            sb.append("CENTER");
        } else {
            if ((gravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
                sb.append("CENTER_VERTICAL|");
            }
            if ((gravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
                sb.append("CENTER_HORIZONTAL|");
            }
            if ((gravity & Gravity.TOP) == Gravity.TOP) {
                sb.append("TOP|");
            }
            if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
                sb.append("LEFT|");
            }
            if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
                sb.append("RIGHT|");
            }
            if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                sb.append("BOTTOM|");
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
     *
     * @param value
     * @return
     */
    public Object layoutSize(int value) {
        if (ViewGroup.LayoutParams.MATCH_PARENT == value) {
            return "match_parent";
        } else if (ViewGroup.LayoutParams.WRAP_CONTENT == value) {
            return "wrap_content";
        } else {
            return value;
        }
    }

    private final String[] RELATIVE_LAYOUT_RULES =
            new String[]{"leftOf", "rightOf", "above", "below", "alignBaseline", "alignLeft", "alignTop", "alignRight", "alignBottom", "alignParentLeft", "alignParentTop", "alignParentRight", "alignParentBottom", "center", "centerHorizontal", "centerVertical"};

    public String relativeLayoutParamRuleName(int index) {
        return "layoutParams_" + (index < RELATIVE_LAYOUT_RULES.length ? RELATIVE_LAYOUT_RULES[index] : index);
    }

    public Object relativeLayoutParamRuleValue(int value) {
        if (RelativeLayout.TRUE == value) {
            return true;
        } else if (value == 0) {
            return "false/NO_ID";
        } else {
            return IdsHelper.getNameForId(value);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public String importantForA11Y(int value) {
        switch (value) {
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

    public Object layerType(int value) {
        switch (value) {
            case View.LAYER_TYPE_HARDWARE:
                return "HW";
            case View.LAYER_TYPE_SOFTWARE:
                return "SW";
            case View.LAYER_TYPE_NONE:
                return "NONE";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    public Object layoutDirection(int value) {
        switch (value) {
            case View.LAYOUT_DIRECTION_INHERIT:
                return "INHERIT";
            case View.LAYOUT_DIRECTION_LOCALE:
                return "LOCALE";
            case View.LAYOUT_DIRECTION_LTR:
                return "LTR";
            case View.LAYOUT_DIRECTION_RTL:
                return "RTL";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    private final int[] LINK_MASKS = new int[]{Linkify.EMAIL_ADDRESSES, Linkify.MAP_ADDRESSES, Linkify.PHONE_NUMBERS, Linkify.WEB_URLS};
    private final String[] LINK_MASKS_VALUES = new String[]{"EMAIL_ADDRESSES", "MAP_ADDRESSES", "PHONE_NUMBERS", "WEB_URLS"};

    public Object linkMask(int autoLinkMask) {
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

    public Object inputType(int inputType) {
        if (inputType == InputType.TYPE_NUMBER_VARIATION_NORMAL) {//0
            return "TYPE_NUMBER_VARIATION_NORMAL";
        }
        StringBuilder sb = new StringBuilder();
        Field[] fields = InputType.class.getFields();
        try {
            for (Field field : fields) {
                if (field.isAccessible() && field.getType() == int.class) {
                    String name = field.getName();
                    int value = field.getInt(null);//
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

    public Object choiceMode(int value) {
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

    public Object drawerLockMode(int value) {
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

    public String stateListFlags(int[] states) {
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

    public Object orientation(int value) {
        switch (value) {
            case LinearLayout.VERTICAL:
                return "VERTICAL";
            case LinearLayout.HORIZONTAL:
                return "HORIZONTAL";
            default:
                return String.format("UNKWNOWN (%s)", value);
        }
    }

    public Object scaleType(int value) {
        return ImageView.ScaleType.values()[value].toString();
    }

    public Object textStyle(int value) {
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

    public Object ellipsize(int value) {
        return TextUtils.TruncateAt.values()[value].toString();
    }

    public Object shape(int value) {
        switch (value) {
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

    public Object fragmentState(int value) {
        switch (value) {
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

    public Object showDividers(int showDividers) {
        if (showDividers == 0) {
            return "NONE";
        }

        StringBuilder sb = new StringBuilder();
        if ((showDividers & LinearLayout.SHOW_DIVIDER_BEGINNING) != 0) {
            sb.append("BEGGINNING|");
        }
        if ((showDividers & LinearLayout.SHOW_DIVIDER_END) != 0) {
            sb.append("END|");
        }
        if ((showDividers & LinearLayout.SHOW_DIVIDER_MIDDLE) != 0) {
            sb.append("MIDDLE|");
        }
        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - 1);
        }
        return sb.toString();
    }

    public Object scrollIndicators(int scrollIndicators) {
        StringBuilder sb = new StringBuilder();

        if ((scrollIndicators & View.SCROLL_INDICATOR_BOTTOM) == View.SCROLL_INDICATOR_BOTTOM) {
            sb.append("SCROLL_INDICATOR_BOTTOM|");
        }

        if ((scrollIndicators & View.SCROLL_INDICATOR_END) == View.SCROLL_INDICATOR_END) {
            sb.append("SCROLL_INDICATOR_END|");
        }

        if ((scrollIndicators & View.SCROLL_INDICATOR_LEFT) == View.SCROLL_INDICATOR_LEFT) {
            sb.append("SCROLL_INDICATOR_LEFT|");
        }

        if ((scrollIndicators & View.SCROLL_INDICATOR_RIGHT) == View.SCROLL_INDICATOR_RIGHT) {
            sb.append("SCROLL_INDICATOR_RIGHT|");
        }

        if ((scrollIndicators & View.SCROLL_INDICATOR_TOP) == View.SCROLL_INDICATOR_TOP) {
            sb.append("SCROLL_INDICATOR_TOP|");
        }

        if ((scrollIndicators & View.SCROLL_INDICATOR_START) == View.SCROLL_INDICATOR_START) {
            sb.append("SCROLL_INDICATOR_START|");
        }

        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - 1);
        } else {
            sb.append("None");
        }

        return sb.toString();
    }

    public Object drawableStates(int[] state) {
        StringBuilder sb = new StringBuilder();
        if (state != null) {
            for (int i = 0, n = state.length; i < n; i++) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                if (state[i] == 0) {
                    sb.append("default");
                } else {
                    sb.append(IdsHelper.getNameForId(state[i]));
                }
            }
        }
        return sb.toString();
    }

    public Object layoutMode(int layoutMode) {
        switch (layoutMode){
            case ViewGroup.LAYOUT_MODE_CLIP_BOUNDS:
                return "LAYOUT_MODE_CLIP_BOUNDS";
            case ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS:
                return "LAYOUT_MODE_OPTICAL_BOUNDS";
            default:
                return "UNKNOWN";
        }
    }

    public Object scrollState(int scrollState) {
        switch (scrollState){
            case android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING:
                return "SCROLL_STATE_DRAGGING";
            case android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE:
                return "SCROLL_STATE_IDLE";
            case android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING:
                return "SCROLL_STATE_SETTLING";
            default:
                return "UNKNOWN";
        }
    }

    public Object gridLayoutOrientation(int orientation) {
        switch (orientation) {
            case android.support.v7.widget.GridLayout.VERTICAL:
                return "VERTICAL";
            case android.support.v7.widget.RecyclerView.HORIZONTAL:
                return "HORIZONTAL";
            default:
                return "UNKNOWN";
        }
    }

    public Object tabMode(int tabMode) {
        switch (tabMode) {
            case android.support.design.widget.TabLayout.MODE_FIXED:
                return "MODE_FIXED";
            case android.support.design.widget.TabLayout.MODE_SCROLLABLE:
                return "MODE_SCROLLABLE";
            default:
                return "UNKNOWN";
        }
    }

    public Object appBarLayoutScrollFlags(int scrollFlags) {
        switch (scrollFlags){
            case android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS:
                return "SCROLL_FLAG_ENTER_ALWAYS";
            case android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED:
                return "SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED";
            case android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED:
                return "SCROLL_FLAG_EXIT_UNTIL_COLLAPSED";
            case android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL:
                return "SCROLL_FLAG_SCROLL";
            case android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP:
                return "SCROLL_FLAG_SNAP";
            default:
                return "UNKNOWN";
        }
    }

    public Object collapseMode(int collapseMode) {
        switch (collapseMode) {
            case android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_OFF:
                return "COLLAPSE_MODE_OFF";
            case android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX:
                return "COLLAPSE_MODE_PARALLAX";
            case android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN:
                return "COLLAPSE_MODE_PIN";
            default:
                return "UNKNOWN";
        }
    }

    public Object focusable(int focusable) {
        switch (focusable) {
            case View.FOCUSABLE:
                return "FOCUSABLE";
            case View.NOT_FOCUSABLE:
                return "NOT_FOCUSABLE";
            case View.FOCUSABLE_AUTO:
                return "FOCUSABLE_AUTO";
            default:
                return "UNKNOWN";
        }
    }

    public Object importantForAutoFill(int importantForAutofill) {
        switch (importantForAutofill) {
            case View.IMPORTANT_FOR_AUTOFILL_AUTO:
                return "IMPORTANT_FOR_AUTOFILL_AUTO";
            case View.IMPORTANT_FOR_AUTOFILL_YES:
                return "IMPORTANT_FOR_AUTOFILL_YES";
            case View.IMPORTANT_FOR_AUTOFILL_NO:
                return "IMPORTANT_FOR_AUTOFILL_NO";
            case View.IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS:
                return "IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS";
            case View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS:
                return "IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS";
            default:
                return "UNKNOWN";
        }
    }
}
