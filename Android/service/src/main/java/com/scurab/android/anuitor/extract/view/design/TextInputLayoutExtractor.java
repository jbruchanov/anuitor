package com.scurab.android.anuitor.extract.view.design;

import com.google.android.material.textfield.TextInputLayout;
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
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        TextInputLayout view = (TextInputLayout) v;

        data.put("Error", view.getError());
        data.put("Hint", view.getHint());
        data.put("PasswordVisibilityToggleContentDescription", view.getPasswordVisibilityToggleContentDescription());
        data.put("PasswordVisibilityToggleDrawable:", view.getPasswordVisibilityToggleDrawable());
        data.put("Typeface:", view.getTypeface());
        data.put("IsCounterEnabled", view.isCounterEnabled());
        data.put("IsErrorEnabled", view.isErrorEnabled());
        data.put("IsHintAnimationEnabled", view.isHintAnimationEnabled());
        data.put("IsHintEnabled", view.isHintEnabled());
        data.put("IsPasswordVisibilityToggleEnabled", view.isPasswordVisibilityToggleEnabled());

        return data;
    }
}
