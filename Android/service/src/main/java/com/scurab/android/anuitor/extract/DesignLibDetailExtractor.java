package com.scurab.android.anuitor.extract;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import com.scurab.android.anuitor.extract.view.design.AppBarLayoutExtractor;
import com.scurab.android.anuitor.extract.view.design.BottomNavigationViewExtractor;
import com.scurab.android.anuitor.extract.view.design.CollapsingToolbarLayoutExtractor;
import com.scurab.android.anuitor.extract.view.design.CoordinatorLayoutExtractor;
import com.scurab.android.anuitor.extract.view.design.DesignLayoutParamsExtractor;
import com.scurab.android.anuitor.extract.view.design.FloatingActionButtonExtractor;
import com.scurab.android.anuitor.extract.view.design.NavigationViewExtractor;
import com.scurab.android.anuitor.extract.view.design.TabLayoutExtractor;
import com.scurab.android.anuitor.extract.view.design.TextInputLayoutExtractor;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class DesignLibDetailExtractor {

    public static void registerExtractors(Translator translator) {
        DetailExtractor.registerExtractor(AppBarLayout.class, new AppBarLayoutExtractor(translator));
        DetailExtractor.registerExtractor(BottomNavigationView.class, new BottomNavigationViewExtractor(translator));
        DetailExtractor.registerExtractor(CollapsingToolbarLayout.class, new CollapsingToolbarLayoutExtractor(translator));
        DetailExtractor.registerExtractor(CoordinatorLayout.class, new CoordinatorLayoutExtractor(translator));
        DetailExtractor.registerExtractor(NavigationView.class, new NavigationViewExtractor(translator));
        DetailExtractor.registerExtractor(FloatingActionButton.class, new FloatingActionButtonExtractor(translator));
        DetailExtractor.registerExtractor(TabLayout.class, new TabLayoutExtractor(translator));
        DetailExtractor.registerExtractor(TextInputLayout.class, new TextInputLayoutExtractor(translator));

        DesignLayoutParamsExtractor.registerExtractors(translator);
    }
}
