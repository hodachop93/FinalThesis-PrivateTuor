package com.hodachop93.hohoda.fragment;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.common.ApplicationConstants;
import com.hodachop93.hohoda.utils.ClickGuard;
import com.hodachop93.hohoda.utils.DateUtils;
import com.hodachop93.hohoda.utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostCoursePageTwo extends Fragment implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    @Bind(R.id.edt_price)
    EditText edtPrice;
    @Bind(R.id.spinner_per)
    Spinner spinnerPer;
    @Bind(R.id.edt_duration)
    EditText edtDuration;
    @Bind(R.id.tv_start_date)
    TextView tvStartDate;
    @Bind(R.id.edt_schedule)
    EditText edtSchedule;
    @Bind(R.id.ll_duration)
    LinearLayout llDuration;
    @Bind(R.id.tv_label_price)
    TextView tvLabelPrice;
    @Bind(R.id.tv_label_duration)
    TextView tvLabelDuration;

    private Calendar startDate;

    public static final int PER_MONTH_TYPE = 0;
    public static final int PER_COURSE_TYPE = 1;

    public static final int DURATION_UNLIMITED = 0;

    public PostCoursePageTwo() {
        // Required empty public constructor
    }

    public static PostCoursePageTwo newInstance() {
        return new PostCoursePageTwo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_course_page_two, container, false);
        ButterKnife.bind(this, view);
        Utils.appendColoredText(tvLabelPrice, " *", Color.RED);
        Utils.appendColoredText(tvLabelDuration, " *", Color.RED);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] unitDurationArr = getResources().getStringArray(R.array.unit_duration_array);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                unitDurationArr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerPer.setAdapter(spinnerAdapter);
        spinnerPer.setOnItemSelectedListener(this);

        ClickGuard.guard(tvStartDate);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            llDuration.setVisibility(View.INVISIBLE);
        } else {
            llDuration.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_start_date)
    public void showDatePicker() {
        startDate = Calendar.getInstance();
        final Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show(getChildFragmentManager(), "DatePicker");
    }

    private void showTimePicker() {
        final Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        timePickerDialog.show(getChildFragmentManager(), "TimePicker");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String getPrice() {
        return edtPrice.getText().toString();
    }

    /**
     * Get per type (/month or /course)
     *
     * @return
     */
    public int getCourseType() {
        if (spinnerPer.getSelectedItemPosition() == 0) {
            return PER_MONTH_TYPE;
        } else {
            return PER_COURSE_TYPE;
        }
    }

    public String getSchedule() {
        return edtSchedule.getText().toString();
    }

    /**
     * Get duration
     *
     * @return the number of months or 0 if unlimited
     */
    public String getDuration() {
        return edtDuration.getText().toString().trim();
    }

    /**
     * Return Start date of a course
     *
     * @return timestamp
     */
    public long getStartDate() {
        if (startDate != null)
            return startDate.getTimeInMillis();
        return -1;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        startDate.set(Calendar.YEAR, year);
        startDate.set(Calendar.MONTH, monthOfYear);
        startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        showTimePicker();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        startDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        startDate.set(Calendar.MINUTE, minute);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_COURSE);
        String date = DateUtils.formatDate(simpleDateFormat, startDate.getTime());
        tvStartDate.setText(date);
    }

    public boolean checkConditionToNext() {
        boolean result = true;
        if (!Utils.checkEditTextNotEmpty(edtPrice)) {
            edtPrice.setError(getString(R.string.field_required));
            result = false;
        }
        if (spinnerPer.getSelectedItemPosition() == 1) {
            if (!Utils.checkEditTextNotEmpty(edtDuration)) {
                edtDuration.setError(getString(R.string.field_required));
                result = false;
            }
        }
        return result;
    }
}
