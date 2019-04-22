package com.scurab.android.anuitorsample;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by JBruchanov on 12/03/2017.
 */

public class CoordinatorLayoutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coordinatorlayout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fab).setOnClickListener(v -> Toast.makeText(v.getContext(), "Click", Toast.LENGTH_LONG).show());

        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) view.findViewById(R.id.navigation).getLayoutParams();
        layoutParams.setBehavior(new BottomBarScrollOffBehavior());
    }

    /**
     * Very simple own behaviour to show/hide BottomNavigationBar based on scroll position
     * It doesn't solve edge case like NestedScrollingParent is not big enough to fully offset BottomNavigationView etc.
     */
    public static class BottomBarScrollOffBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {

        private int mInitialTop = Integer.MIN_VALUE;
        private int mLastValue = 0;
        private int mInitialChildTop = 0;

        public BottomBarScrollOffBehavior() {
        }

        public BottomBarScrollOffBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, BottomNavigationView child, View dependency) {
            return dependency instanceof NestedScrollingParent;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, BottomNavigationView child, View dependency) {
            //dependency is NestedScrollView
            //take top and scroll so if it's moved or inner-scrolled we will take it as an event for ours
            final int actualTop = -dependency.getTop() + dependency.getScrollY();
            //init values for calculation differences
            if (mInitialTop == Integer.MIN_VALUE) {
                mInitialTop = actualTop;
                mLastValue = actualTop - mInitialTop;
                mInitialChildTop = child.getTop();
            }

            //take the absolute difference of initial position and current position
            int absoluteDiff = actualTop - mInitialTop;
            //simple difference from last call
            int relativeDiff = mLastValue - absoluteDiff;
            //new absolute top
            int newAbsoluteTop = child.getTop() - relativeDiff;
            //maximum top, to keep bottom navigation tab bar aligned with bottom edge, just outside display
            final int maxAbsoluteTop = mInitialChildTop + child.getHeight();

            boolean handled = false;
            //are we in range ? => BottomNavigationView is always "touching" bottom edge of display
            //FIXME: catch last diff value when we need to offset less than relativeDiffs
            if (mInitialChildTop <= newAbsoluteTop && newAbsoluteTop <= maxAbsoluteTop && relativeDiff != 0) {
                child.offsetTopAndBottom(-relativeDiff);
                handled = true;
            }
            mLastValue = absoluteDiff;
            return handled;
        }
    }
}
