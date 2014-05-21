package com.scurab.gwt.anuitor.client.model;

/**
 * Simple Rectangle representation for out purpose
 * @author jbruchanov
 *
 */
public class Rect {
    public final int left;
    public final int top;
    public final int right;
    public final int bottom;
    public final int width;
    public final int height;

    /**
     * 
     * @param x
     * @param y
     * @param w width
     * @param h height
     */
    public Rect(int x, int y, int w, int h) {
        left = x;
        top = y;
        width = w;
        height = h;
        right = x + w;
        bottom = y + h;
    }

    /**
     * Create rectangle from view (handles view scale)
     * @param view
     * @return
     */
    public static Rect fromView(ViewNodeJSO view) {
        return fromView(view, true);
    }

    /**
     * Create rectangle from view
     * @param view
     * @param withScaling true to make rectangle scaled as view is...
     * @return
     */
    public static Rect fromView(ViewNodeJSO view, boolean withScaling) {
        int x = (int)view.getDouble(ViewFields.LOCATION_SCREEN_X);
        int y = (int)view.getDouble(ViewFields.LOCATION_SCREEN_Y);
        int width = (int)view.getDouble(ViewFields.WIDTH);
        int height = (int)view.getDouble(ViewFields.HEIGHT);

        if (withScaling) {            
            double scaleX = view.hasKey(ViewFields.Internal.SCALE_X) ? view.getDouble(ViewFields.Internal.SCALE_X) : 1.0;
            double scaleY = view.hasKey(ViewFields.Internal.SCALE_Y) ? view.getDouble(ViewFields.Internal.SCALE_Y) : 1.0;
            width *= scaleX;
            height *= scaleY;
        }

        Rect r = new Rect(x, y, width, height);
        return r;
    }

    public boolean contains(int x, int y) {
        return left <= x && x <= right && top <= y && y <= bottom;
    }

    public Rect scale(float mScale) {
        return new Rect((int) (left * mScale), (int) (top * mScale), (int) (width * mScale), (int) (height * mScale));
    }
}
