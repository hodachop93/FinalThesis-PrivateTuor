package com.hodachop93.hohoda.fragment;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.HashTagEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostCoursePageOne extends Fragment {
    @Bind(R.id.edt_title)
    EditText edtTitle;
    @Bind(R.id.edt_hashtag)
    HashTagEditText edtHashTag;
    @Bind(R.id.tv_label_tag)
    TextView tvLabelTag;
    @Bind(R.id.tv_label_title)
    TextView tvLabelTitle;

    public PostCoursePageOne() {
        // Required empty public constructor
    }

    public static PostCoursePageOne newInstance(HashTag hashTag) {
        PostCoursePageOne fragment = new PostCoursePageOne();
        Bundle bundle = new Bundle();
        bundle.putParcelable("HASH_TAG", hashTag);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_course_page_one, container, false);
        ButterKnife.bind(this, view);
        Utils.appendColoredText(tvLabelTitle, " *", Color.RED);
        Utils.appendColoredText(tvLabelTag, " *", Color.RED);

        HashTag hashTag = getArguments().getParcelable("HASH_TAG");
        edtHashTag.addObject(hashTag);
        return view;
    }

    public String getTitle() {
        return edtTitle.getText().toString();
    }

    public List<String> getHashTags() {
        List<HashTag> hashTagList = edtHashTag.getObjects();
        List<String> result = new ArrayList<>();
        for (HashTag item : hashTagList) {
            result.add(item.getName());
        }
        return result;
    }

    public boolean checkConditionToNext() {
        boolean result = true;
        if (!Utils.checkEditTextNotEmpty(edtTitle)) {
            edtTitle.setError(getString(R.string.field_required));
            result = false;
        }
        if (edtHashTag.getObjects().size() == 0) {
            edtHashTag.setError(getString(R.string.field_required));
            result = false;
        }
        return result;
    }
}
