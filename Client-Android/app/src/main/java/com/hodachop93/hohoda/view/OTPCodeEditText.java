package com.hodachop93.hohoda.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.hodachop93.hohoda.R;

import java.util.ArrayList;
import java.util.List;


public class OTPCodeEditText extends LinearLayout {

    private int mNumDigits = 6;
    private int currentDigitIndex = 0;
    private List<DigitField> digitFields;
    private int[] digits;

    public OTPCodeEditText(Context context) {
        super(context);
        init();
    }

    public OTPCodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OTPCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        removeAllViews();

        setOrientation(HORIZONTAL);

        digitFields = new ArrayList<>();
        currentDigitIndex = 0;
        digits = new int[mNumDigits];

        for (int i = 0; i < mNumDigits; i++) {

            digits[i] = -1;

            DigitField digitField = new DigitField();
            digitFields.add(digitField);
            addView(digitField);
        }

        digitFields.get(0).setSelected(true);

    }

    private void setActiveDigit(int index) {
        currentDigitIndex = index;
        for (int i = 0; i < mNumDigits; i++) {
            digitFields.get(i).setSelected(i == index);
            digitFields.get(i).setDigit(digits[i]);
        }
    }

    public void append(int digit) {
        digits[currentDigitIndex] = digit;
        if (currentDigitIndex < mNumDigits - 1)
            currentDigitIndex++;
        setActiveDigit(currentDigitIndex);
    }

    public void popBackDigit() {
        if (currentDigitIndex == 0) return;
        if (currentDigitIndex == mNumDigits - 1) {
            if (digitFields.get(currentDigitIndex).getDigit() == -1) {
                currentDigitIndex--;
            }
            digits[currentDigitIndex] = -1;
        } else {
            currentDigitIndex--;
            digits[currentDigitIndex] = -1;
        }

        setActiveDigit(currentDigitIndex);
    }

    public String getEnteredCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mNumDigits; i++)
            if (digits[i] > -1) builder.append(String.valueOf(digits[i]));
        return builder.toString();
    }

    public boolean isFinishedInput() {
        return getEnteredCode().length() == mNumDigits;
    }

    public void clear() {
        init();
    }

    private class DigitField extends LinearLayout {

        private TextView tvDigit;
        private View indicator;

        public DigitField() {
            super(OTPCodeEditText.this.getContext());
            init();
        }

        private void init() {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(R.layout.layout_editable_digit, DigitField.this, true);

            tvDigit = (TextView) findViewById(R.id.tv_digit);
            indicator = findViewById(R.id.indicator);

            setSelected(false);
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);

            indicator.setSelected(selected);
        }

        public int getDigit() {
            int digit;
            try {
                digit = Integer.parseInt(tvDigit.getText().toString());
            } catch (NumberFormatException e) {
                digit = -1;
            }
            return digit;
        }

        public void setDigit(int digit) {
            tvDigit.setText(digit > -1 ? String.valueOf(digit) : "");
        }
    }

}
