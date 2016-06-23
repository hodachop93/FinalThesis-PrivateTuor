package com.hodachop93.hohoda.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hodachop93.hohoda.HohodaApplication;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.Course;
import com.hodachop93.hohoda.utils.DateUtils;
import com.hodachop93.hohoda.utils.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyCourseAdapter extends BaseLoadMoreAdapter<Course> {

    private OnCourseClickListener mListener;
    private Context mContext;

    public MyCourseAdapter(List<Course> data, RecyclerView recyclerView) {
        super(data, recyclerView);

        recyclerView.setAdapter(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_my_course_item, parent, false);
        this.mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final Course course) {
        if (!(holder instanceof ViewHolder)) {
            throw new ClassCastException();
        }
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.tvCourseStatus.setText(course.getStatusString());
        viewHolder.tvTitle.setText(course.getTitle());
        if (course.getAddress() != null && !course.getAddress().isEmpty()) {
            viewHolder.tvAddress.setText(course.getAddress());
            viewHolder.tvAddress.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvAddress.setVisibility(View.GONE);
        }
        Utils.fillHashtagList(viewHolder.tvHashTag, course.getHashtags(),
                ContextCompat.getColor(HohodaApplication.getInstance().getApplicationContext(), R.color.colorPrimary));
        viewHolder.tvPrice.setText(getCoursePrice(course));
        viewHolder.tvBidAndTimeAgo.setText(getBidAndTimeAgo(course));
        Utils.changeBackgroundCourseStatus(mContext, viewHolder.tvCourseStatus, course.getStatusInt());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCourseClick(course);
                }
            }
        });
    }

    public void setOnCourseClickListener(OnCourseClickListener listener) {
        mListener = listener;
    }

    private String getBidAndTimeAgo(Course course) {
        String result = DateUtils.getTimeAgo(course.getCreatedAt());
        result += " - " + course.getBid() + " " + (course.getBid() >= 2 ? "people" : "person") + " applied";
        return result;
    }

    private String getCoursePrice(Course course) {
        String unit = "K VND/";
        if (course.getCourseType() == Course.COURSE_TYPE_PER_MONTH) {
            return course.getPrice() + unit + "month";
        } else {
            return course.getPrice() + unit + "course";
        }
    }

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_course_status)
        TextView tvCourseStatus;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.tv_hash_tag)
        TextView tvHashTag;
        @Bind(R.id.tv_price)
        TextView tvPrice;
        @Bind(R.id.tv_bid_and_time_ago)
        TextView tvBidAndTimeAgo;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
