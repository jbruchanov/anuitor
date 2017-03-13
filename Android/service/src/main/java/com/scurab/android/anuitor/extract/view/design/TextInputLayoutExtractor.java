package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.LinearLayoutExtractor;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class TextInputLayoutExtractor extends LinearLayoutExtractor {

    public TextInputLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        final HashMap<String, Object> result = super.fillValues(v, data, parentData);

        TextInputLayout view = (TextInputLayout) v;

        result.put("Error", view.getError());
        result.put("Hint", view.getHint());
        result.put("PasswordVisibilityToggleContentDescription", view.getPasswordVisibilityToggleContentDescription());
        result.put("PasswordVisibilityToggleDrawable:", view.getPasswordVisibilityToggleDrawable());
        result.put("Typeface:", view.getTypeface());
        result.put("IsCounterEnabled", view.isCounterEnabled());
        result.put("IsErrorEnabled", view.isErrorEnabled());
        result.put("IsHintAnimationEnabled", view.isHintAnimationEnabled());
        result.put("IsHintEnabled", view.isHintEnabled());
        result.put("IsPasswordVisibilityToggleEnabled", view.isPasswordVisibilityToggleEnabled());

        return result;
    }
}
