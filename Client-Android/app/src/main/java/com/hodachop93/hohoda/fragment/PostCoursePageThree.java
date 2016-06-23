package com.hodachop93.hohoda.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.adapter.GooglePlaceAutoAdapter;
import com.hodachop93.hohoda.api.googleplace.GooglePlaceApi;
import com.hodachop93.hohoda.model.GooglePlace;
import com.hodachop93.hohoda.model.GooglePlaceResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostCoursePageThree extends Fragment {
    @Bind(R.id.edt_address)
    AutoCompleteTextView edtAddress;
    @Bind(R.id.edt_des)
    EditText edtDescription;

    private GooglePlaceAutoAdapter mGooglePlaceAdapter;
    private List<GooglePlace> mGooglePlaceList;
    private GooglePlace mAddress;

    private Callback<GooglePlaceResponse> mCallbackGooglePlace = new Callback<GooglePlaceResponse>() {
        @Override
        public void onResponse(Call<GooglePlaceResponse> call, Response<GooglePlaceResponse> response) {
            List<GooglePlace> googlePlaces = response.body().getPredictions();

            mGooglePlaceList.clear();
            mGooglePlaceList.addAll(googlePlaces);
            mGooglePlaceAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Call<GooglePlaceResponse> call, Throwable t) {
            mGooglePlaceList.clear();
            mGooglePlaceAdapter.notifyDataSetChanged();
        }
    };

    public PostCoursePageThree() {
        // Required empty public constructor
    }

    public static PostCoursePageThree newInstance() {
        return new PostCoursePageThree();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_course_page_three, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGooglePlaceList = new ArrayList<>();
        mGooglePlaceAdapter = new GooglePlaceAutoAdapter(getActivity(), mGooglePlaceList);
        edtAddress.setAdapter(mGooglePlaceAdapter);

        edtAddress.setThreshold(3);
        edtAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = getActivity().getString(R.string.google_server_key);
                Call<GooglePlaceResponse> call = GooglePlaceApi.getInstance().getGooglePlace(s.toString(),
                        "geocode", key);
                call.enqueue(mCallbackGooglePlace);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mGooglePlaceList != null) {
                    mAddress = mGooglePlaceList.get(position);
                }
            }
        });
    }

    public GooglePlace getGooglePlaceAddress() {
        return mAddress;
    }

    public String getAddress() {
        return edtAddress.getText().toString();
    }

    public String getDescription() {
        return edtDescription.getText().toString();
    }

}
