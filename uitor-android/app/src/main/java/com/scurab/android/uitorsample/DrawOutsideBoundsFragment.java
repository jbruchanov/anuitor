package com.scurab.android.uitorsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DrawOutsideBoundsFragment extends Fragment {

    private TextView mTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout frameLayout = new FrameLayout(inflater.getContext());
        frameLayout.setClipChildren(false);

        mTextView = new HelpTextView(frameLayout.getContext());
        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        mTextView.setPadding(pad, pad, pad, pad);
        mTextView.setText("Circle drawn\noutside bounds!");
        mTextView.setTextColor(Color.BLACK);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        frameLayout.addView(mTextView, lp);
        return frameLayout;
    }

    public static class HelpTextView extends AppCompatTextView {

        private Paint mPaint;

        public HelpTextView(Context context) {
            super(context);

            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0x80FF0000);
            mPaint.setStrokeWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getRad(), mPaint);
        }

        private float getRad() {
            return Math.max(getWidth(), getHeight()) * 0.75f;
        }

        public void getDrawingSize(Rect outArea) {
            int rad = (int) Math.ceil(getRad());
            int cx = (int) (0.5f + getWidth() / 2);
            int cy = (int) (0.5f + getHeight() / 2);
            outArea.set(cx - rad, cy - rad, cx + rad, cy + rad);
        }
    }
}

