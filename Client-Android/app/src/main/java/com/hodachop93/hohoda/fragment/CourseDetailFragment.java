package com.hodachop93.hohoda.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.eventbus.CourseDetailEventBus;
import com.hodachop93.hohoda.model.Course;
import com.hodachop93.hohoda.utils.DateUtils;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hopho on 20/04/2016.
 */
public class CourseDetailFragment extends Fragment {
    @Bind(R.id.tv_description)
    TextView tvDescription;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.tv_start_date)
    TextView tvStartDate;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_schedule)
    TextView tvSchedule;

    public CourseDetailFragment() {
    }

    public static CourseDetailFragment newInstance() {
        CourseDetailFragment instance = new CourseDetailFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        CourseDetailEventBus.getInstance().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onCourseDetailEventBus(CourseDetailEventBus.Event event) {
        Course course = event.getCourse();
        fillData(course);
    }

    private void fillData(Course course) {
        tvAddress.setText(course.getAddress());
        tvDescription.setText(course.getDescription());
        tvPrice.setText(getCoursePrice(course));
        tvStartDate.setText(DateUtils.formatDateCourse(course.getStartDate()));
        tvSchedule.setText(course.getSchedule());
    }

    private String getCoursePrice(Course course) {
        String unit = "K VND/";
        if (course.getCourseType() == Course.COURSE_TYPE_PER_MONTH) {
            return course.getPrice() + unit + "month";
        } else {
            return course.getPrice() + unit + course.getDuration();
        }
    }

    @Override
    public void onStop() {
        CourseDetailEventBus.getInstance().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
