package com.hodachop93.hohoda.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.hodachop93.hohoda.R;


public class NumberKeyBoardView extends LinearLayout implements View.OnClickListener {

    private KeyboardViewListener mKeyboardViewListener;

    private View btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnCloseKeyboard, btnBackSpace;

    public NumberKeyBoardView(Context context) {
        super(context);
        init();
    }

    public NumberKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_number_keyboard, this, true);

        btn0 = findViewById(R.id.btn_0);
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);
        btn8 = findViewById(R.id.btn_8);
        btn9 = findViewById(R.id.btn_9);
        btnCloseKeyboard = findViewById(R.id.btn_close_keyboard);
        btnBackSpace = findViewById(R.id.btn_back_space);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnBackSpace.setOnClickListener(this);
        btnCloseKeyboard.setOnClickListener(this);
    }

    public void setKeyboardViewListener(KeyboardViewListener keyboardViewListener) {
        mKeyboardViewListener = keyboardViewListener;
    }

    @Override
    public void onClick(View v) {
        if (v == btn0) {
            mKeyboardViewListener.onDigitHit(0);
        } else if (v == btn1) {
            mKeyboardViewListener.onDigitHit(1);
        } else if (v == btn2) {
            mKeyboardViewListener.onDigitHit(2);
        } else if (v == btn3) {
            mKeyboardViewListener.onDigitHit(3);
        } else if (v == btn4) {
            mKeyboardViewListener.onDigitHit(4);
        } else if (v == btn5) {
            mKeyboardViewListener.onDigitHit(5);
        } else if (v == btn6) {
            mKeyboardViewListener.onDigitHit(6);
        } else if (v == btn7) {
            mKeyboardViewListener.onDigitHit(7);
        } else if (v == btn8) {
            mKeyboardViewListener.onDigitHit(8);
        } else if (v == btn9) {
            mKeyboardViewListener.onDigitHit(9);
        } else if (v == btnCloseKeyboard) {
            mKeyboardViewListener.onCloseKeyboard();
        } else if (v == btnBackSpace) {
            mKeyboardViewListener.onBackSpace();
        }
    }

    public interface KeyboardViewListener {
        void onDigitHit(int digit);

        void onCloseKeyboard();

        void onBackSpace();
    }
}
