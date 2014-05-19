package com.scurab.gwt.anuitor.client.util;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.scurab.gwt.anuitor.client.model.Rect;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

/**
 * Help tools for working with Canvas
 * @author jbruchanov
 *
 */
public final class CanvasTools{

    /**
     * Draw custom rectangle
     * @param canvas
     * @param x
     * @param y
     * @param w width
     * @param h height
     * @param stroke strokeColor
     * @param fill fillColor
     */
    public static void drawRectangle(Canvas canvas, int x, int y, int w, int h, String stroke, String fill) {
        Context2d c = canvas.getContext2d();
        c.setStrokeStyle(stroke);
        drawHorizontalLine(c, x, y, w);
        drawHorizontalLine(c, x, y + h, w);
        drawVerticalLine(c, x, y, h);
        drawVerticalLine(c, x + w, y, h);

        c.setGlobalAlpha(0.3);
        c.setFillStyle(fill);
        c.fillRect(x, y, w, h);
        c.setGlobalAlpha(1);
    }    
    
    /**
     * Draw red grid
     * @param canvas
     * @param step pixels between lines
     */
    public static void drawGrid(Canvas canvas, int step) {
        Context2d c = canvas.getContext2d();
        int w = canvas.getCoordinateSpaceWidth();
        int h = canvas.getCoordinateSpaceHeight();

        c.setLineWidth(1);
        c.setGlobalAlpha(0.5);
        c.setStrokeStyle(HTMLColors.RED);

        for (int i = step; i < h; i += step) {
            drawHorizontalLine(c, 0, i, w);
            drawVerticalLine(c, i, 0, h);
        }
        c.setGlobalAlpha(1);
    }
    
    /**
     * Draw vertical line
     * @param c
     * @param x start point
     * @param y start point
     * @param height
     */
    public static void drawVerticalLine(Context2d c, int x, int y, int height) {        
        c.beginPath();
        c.moveTo(x, y);
        c.lineTo(x, y + height);
        c.closePath();
        c.stroke();
    }

    /**
     * Draw horizontal line
     * @param c
     * @param x start point
     * @param y start point
     * @param width
     */
    public static void drawHorizontalLine(Context2d c, int x, int y, int width) {        
        c.beginPath();
        c.moveTo(x, y);
        c.lineTo(x + width, y);
        c.closePath();
        c.stroke();
    }
    
    /**
     * 
     * @param view
     * @param canvas
     * @param scale
     * @param stroke
     * @param fill
     */
    public static void drawRectForView(ViewNodeJSO view, Canvas canvas, float scale, String stroke, String fill) {
        if (view == null) {
            return;
        }

        Rect r = Rect.fromView(view);
        if (scale != 1f) {
            r = r.scale(scale);
        }        
        CanvasTools.drawRectangle(canvas, r.left, r.top, r.width, r.height, stroke, fill);
    }
}
