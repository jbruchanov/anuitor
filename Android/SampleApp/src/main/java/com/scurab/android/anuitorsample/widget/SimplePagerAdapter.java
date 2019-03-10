package com.scurab.android.anuitorsample.widget;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.Arrays;

/**
 * User: jbruchanov
 * Date: 26/06/14
 * Time: 13:02
 */

@SuppressWarnings("unchecked")
public abstract class SimplePagerAdapter<T extends View> extends PagerAdapter {

    private View[] mViews = new View[8];
    private int mCurrentIndex;
    private T mCurrentView;

    private Context mContext;

    public SimplePagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public T instantiateItem(ViewGroup container, int position) {
        T v = ensureView(position);
        if (container != null) {
            container.addView(v);
        }
        return v;
    }

    /**
     * Return and create if necessary page view
     *
     * @param position
     * @return
     */
    T ensureView(int position) {
        ensureCorrectArray(position);
        if (mViews[position] == null) {
            mViews[position] = onCreateView(position, null);
        }
        return (T) mViews[position];
    }

    private void ensureCorrectArray(int position) {
        if (mViews.length <= position) {
            View[] newViews = new View[mViews.length * 2];
            System.arraycopy(mViews, 0, newViews, 0, mViews.length);
            mViews = newViews;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        @SuppressWarnings("unchecked")
        T v = (T) object;
        removeFromParent(v);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentIndex = position;
        mCurrentView = (T) object;
    }

    protected void removeFromParent(View v) {
        ViewParent parent = v.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(v);
        }
    }

    public abstract T onCreateView(int position, View container);


    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public T getCurrentView() {
        return mCurrentView;
    }

    public T obtainItem(int position) {
        /*
            In case of changing adapter, we can call obtainItem during bulk insert sooner than
            ViewPager calls instantiateItem so for this case, we have to create page view manually.
         */
        return ensureView(position);
    }

    public T getItem(int position) {
        return position < mViews.length ? (T) mViews[position] : null;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void notifyDataSetChanged() {
        Arrays.fill(mViews, null);//clear the array to let recreated views
        super.notifyDataSetChanged();
    }

    protected boolean removeItem(int position) {
        if (mViews[position] != null) {
            mViews[position] = null;
            return true;
        }
        return false;
    }
}