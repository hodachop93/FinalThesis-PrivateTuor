package com.hodachop93.hohoda.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hodachop93.hohoda.HohodaApplication;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.Course;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.DateUtils;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class BrowseCourseAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<Course> mCourseList;
    private Context mContext;


    private OnCourseClickListener mListener;


    public BrowseCourseAdapter(List<Course> courseList, Context context) {
        this.mCourseList = courseList;
        this.mContext = context;
    }

    @Override
    public View getHeaderView(final int position, View convertView, ViewGroup parent) {
        HeaderViewHolder headerVH = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_browse_course_item_header,
                    parent, false);
            headerVH = new HeaderViewHolder(convertView);
            convertView.setTag(headerVH);
        } else {
            headerVH = (HeaderViewHolder) convertView.getTag();
        }
        setValueHeader(headerVH, position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onProfileClick(mCourseList.get(position).getOwnerId());
                }
            }
        });

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return (mCourseList == null) ? 0 : mCourseList.size();
    }

    @Override
    public Object getItem(int position) {
        return (mCourseList == null) ? null : mCourseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ContentViewHolder contentVH = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_browse_course_item_content,
                    parent, false);
            contentVH = new ContentViewHolder(convertView);
            convertView.setTag(contentVH);
        } else {
            contentVH = (ContentViewHolder) convertView.getTag();
        }
        setValueContent(contentVH, position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCourseClick(mCourseList.get(position));
                }
            }
        });
        return convertView;
    }


    private void setValueHeader(HeaderViewHolder headerVH, int position) {
        Course course = mCourseList.get(position);
        Profile ownerProfile = course.getProfile();

        Picasso.with(mContext)
                .load(Utils.convertUrlIfUsingLocalhost(ownerProfile.getAvatarUrl()))
                .placeholder(R.drawable.ic_user_default)
                .error(R.drawable.ic_user_default)
                .into(headerVH.cimvAvatar);
        headerVH.tvName.setText(ownerProfile.getName());
        headerVH.tvTimeAgo.setText(DateUtils.getTimeAgo(course.getCreatedAt()));
        headerVH.ratingBar.setRating(ownerProfile.getRateAverage());
    }

    private void setValueContent(ContentViewHolder contentVH, int position) {
        Course course = mCourseList.get(position);

        contentVH.tvTitle.setText(course.getTitle());
        if (course.getAddress()==null || course.getAddress().isEmpty()){
            contentVH.tvAddress.setVisibility(View.GONE);
        }else{
            contentVH.tvAddress.setText(course.getAddress());
            contentVH.tvAddress.setVisibility(View.VISIBLE);
        }
        contentVH.tvBid.setText(getBid(course.getBid()));
        Utils.fillHashtagList(contentVH.tvHashTag, course.getHashtags(), ContextCompat.getColor(HohodaApplication.getInstance().getApplicationContext(), R.color.colorPrimary));
        contentVH.tvPrice.setText(getCoursePrice(course));
        contentVH.tvCourseStatus.setText(course.getStatusString());
        Utils.changeBackgroundCourseStatus(mContext, contentVH.tvCourseStatus, course.getStatusInt());
    }

    private String getCoursePrice(Course course) {
        String unit = "K VND/";
        if (course.getCourseType() == Course.COURSE_TYPE_PER_MONTH) {
            return course.getPrice() + unit + "month";
        } else {
            return course.getPrice() + unit + "course";
        }
    }

    private String getBid(int bid) {
        return bid + ((bid > 2) ? " people" : " person") + " applied";
    }


    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.mListener = listener;
    }

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
        void onProfileClick(String userId);
    }


    static class HeaderViewHolder {
        @Bind(R.id.cimv_avatar)
        CircleImageView cimvAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_time_ago)
        TextView tvTimeAgo;
        @Bind(R.id.rating_bar)
        RatingBar ratingBar;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ContentViewHolder {
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
        @Bind(R.id.tv_bid)
        TextView tvBid;

        public ContentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
