package com.adobe.intelliscan.utils;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.adobe.intelliscan.R;

/**
 * Created by bento on 17/04/17.
 */

public class CustomRadioButton extends AppCompatRadioButton {

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Don't show the radiobutton.
        setButtonDrawable(null);
    }

    public CustomRadioButton(Context context) {
        super(context);

        // Don't show the radiobutton.
        setButtonDrawable(null);
    }

    @Override
    public void setChecked(boolean isChecked) {
        if (isChecked) {
            this.setBackgroundResource(R.drawable.radio_checked);
        } else {
            this.setBackgroundResource(R.drawable.radio_unchecked);
        }
        super.setChecked(isChecked);
    }
}