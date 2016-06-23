package com.hodachop93.hohoda.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hodachop93.hohoda.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hopho on 29/03/2016.
 */
public class ProcessStepView extends LinearLayout {
    @Bind(R.id.tv_step_1)
    TextView tv1;
    @Bind(R.id.tv_step_2)
    TextView tv2;
    @Bind(R.id.tv_step_3)
    TextView tv3;

    private int mCurrentStep;
    private OnDoneListener mListener;

    public ProcessStepView(Context context) {
        super(context);
        init();
    }

    public ProcessStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProcessStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_process_step, this);
        ButterKnife.bind(this);
        mCurrentStep = 1;
        tv1.setText("1");
        tv2.setText("2");
        tv3.setText("3");
    }

    public void nextStep() {
        switch (mCurrentStep) {
            case 1:
                tv1.setText("");
                tv1.setSelected(true);
                mCurrentStep = 2;
                break;
            case 2:
                tv2.setText("");
                tv2.setSelected(true);
                mCurrentStep = 3;
                break;
            case 3:
                mListener.done();
        }
    }

    public void backToPreviousStep() {
        switch (mCurrentStep) {
            case 2:
                tv1.setText("1");
                tv1.setSelected(false);
                mCurrentStep = 1;
                break;
            case 3:
                tv2.setText("2");
                tv2.setSelected(false);
                mCurrentStep = 2;
                break;

        }
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    public void setOnDoneListener(OnDoneListener mListener) {
        this.mListener = mListener;
    }

    public interface OnDoneListener {
        void done();
    }
}
