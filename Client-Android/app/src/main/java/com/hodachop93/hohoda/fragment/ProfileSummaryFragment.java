package com.hodachop93.hohoda.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.UserProfileActivity;
import com.hodachop93.hohoda.adapter.GooglePlaceAutoAdapter;
import com.hodachop93.hohoda.api.googleplace.GooglePlaceApi;
import com.hodachop93.hohoda.api.profile.ProfileApi;
import com.hodachop93.hohoda.api.profile.UpdateUserProfileRequestBody;
import com.hodachop93.hohoda.eventbus.ProfileEventBus;
import com.hodachop93.hohoda.exception.ErrorManager;
import com.hodachop93.hohoda.model.GooglePlace;
import com.hodachop93.hohoda.model.GooglePlaceResponse;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.HashTagEditText;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSummaryFragment extends BaseFragment {
    @Bind(R.id.edt_full_name)
    EditText edtFullName;
    @Bind(R.id.edt_address)
    AutoCompleteTextView edtAddress;
    @Bind(R.id.edt_phone)
    EditText edtPhone;
    @Bind(R.id.edt_interested_subject)
    HashTagEditText edtInterestedSubject;
    @Bind(R.id.edt_summary)
    EditText edtSummary;
    @Bind(R.id.edt_age)
    EditText edtAge;
    @Bind(R.id.spinner_gender)
    AppCompatSpinner spinnerGender;

    private ProgressDialog progressDialog;

    private GooglePlaceAutoAdapter mGooglePlaceAdapter;
    private List<GooglePlace> mGooglePlaceList;
    private GooglePlace mAddress;
    private Bitmap mAvatar;

    private Callback<HohodaResponse<Object>> mCallbackUpdateProfile = new Callback<HohodaResponse<Object>>() {
        @Override
        public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {
            if (progressDialog != null)
                progressDialog.dismiss();

            if (response.isSuccess()) {
                String status = response.body().getStatus();
                if ("0".equals(status)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_update_profile_success), Toast.LENGTH_SHORT).show();
                    Activity activity = getActivity();
                    if (activity instanceof UserProfileActivity) {
                        UserProfileActivity userProfileActivity = (UserProfileActivity) activity;
                        userProfileActivity.getSupportActionBar().setTitle(edtFullName.getText());
                    }
                } else {
                    ErrorManager.handleApplicationException(getActivity(), response);
                }
            } else {
                ErrorManager.handleErroneousException(getActivity(), response);
            }
        }

        @Override
        public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {
            if (progressDialog != null)
                progressDialog.dismiss();

            ErrorManager.handleNetworkException(getActivity());
        }
    };

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

    private Callback<JsonObject> mCallbackGooglePlaceId = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            if (response.isSuccess()) {
                JsonObject location = response.body().getAsJsonObject("result").getAsJsonObject("geometry").getAsJsonObject("location");
                double lat = location.getAsJsonPrimitive("lat").getAsDouble();
                double lng = location.getAsJsonPrimitive("lng").getAsDouble();
                mAddress.setPosition(new LatLng(lat, lng));
            }
            makeUpdateProfileRequest(mAvatar);
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            makeUpdateProfileRequest(mAvatar);
        }
    };

    public ProfileSummaryFragment() {
    }

    public static ProfileSummaryFragment newInstance() {
        return new ProfileSummaryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_summary, container, false);
        ButterKnife.bind(this, view);
        Utils.hideSoftKeyboard(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] genderArray = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                genderArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerGender.setAdapter(spinnerAdapter);
        spinnerGender.setEnabled(false);

        edtInterestedSubject.allowCollapse(false);

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

    @Override
    public void onStart() {
        super.onStart();
        ProfileEventBus.getInstance().register(this);
    }

    @Override
    public void onStop() {
        if (ProfileEventBus.getInstance().isRegistered(this)) {
            ProfileEventBus.getInstance().unregister(this);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUpdateProfileEventBus(ProfileEventBus.UpdateProfileEvent event) {
        Profile profile = event.getProfile();
        if (profile != null) {
            fillData(profile);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUpdatePersonProfileEventBus(ProfileEventBus.UpdatePersonProfileEvent event) {
        Profile profile = event.getProfile();
        if (profile != null) {
            fillData(profile);
        }
        if (ProfileEventBus.getInstance().isRegistered(this)) {
            ProfileEventBus.getInstance().unregister(this);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEditProfileEvent(ProfileEventBus.EditProfileEvent event) {
        boolean enable = event.isEnable();
        mAvatar = event.getAvatar();
        if (enable) {
            enableEditProfile();
        } else {
            disableEditProfile();
            //get lat lng from google place id
            if (mAddress != null)
                makeGetLatLngFromGooglePlaceId(mAddress.getPlaceId());
            makeUpdateProfileRequest(mAvatar);
        }
    }

    private void makeGetLatLngFromGooglePlaceId(String placeId) {
        String key = getActivity().getString(R.string.google_server_key);
        Call<JsonObject> call = GooglePlaceApi.getInstance().getLatLngFromGooglePlaceId(placeId, key);
        call.enqueue(mCallbackGooglePlaceId);
    }

    private void makeUpdateProfileRequest(Bitmap avatar) {
        UpdateUserProfileRequestBody body = new UpdateUserProfileRequestBody();
        body.setName(edtFullName.getText().toString().trim());
        body.setAddress(edtAddress.getText().toString().trim());
        body.setAge(Utils.checkEditTextNotEmpty(edtAge) ? edtAge.getText().toString().trim() : null);
        body.setGender(spinnerGender.getSelectedItemPosition());
        body.setPhone(edtPhone.getText().toString().trim());
        body.setInfor(edtSummary.getText().toString().trim());
        body.setInterestedSubjects(edtInterestedSubject.getObjects());
        if (mAddress != null) {
            body.setLat(String.valueOf(mAddress.getPosition().latitude));
            body.setLng(String.valueOf(mAddress.getPosition().longitude));
        }

        if (avatar != null) {
            body.setImageString(avatar);
        }

        progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.updatting));
        Call<HohodaResponse<Object>> call = ProfileApi.getInstance().updateUserProfile(body);
        call.enqueue(mCallbackUpdateProfile);
    }

    private void disableEditProfile() {
        edtFullName.setEnabled(false);
        edtAddress.setEnabled(false);
        edtAge.setEnabled(false);
        edtInterestedSubject.setEnabled(false);
        edtSummary.setEnabled(false);
        edtPhone.setEnabled(false);
        spinnerGender.setEnabled(false);
    }

    private void enableEditProfile() {
        edtFullName.setEnabled(true);
        edtAddress.setEnabled(true);
        edtAge.setEnabled(true);
        edtInterestedSubject.setEnabled(true);
        edtSummary.setEnabled(true);
        edtPhone.setEnabled(true);
        spinnerGender.setEnabled(true);
        edtFullName.requestFocus();
    }

    private void fillData(Profile profile) {
        edtFullName.setText(profile.getName());
        edtAddress.setText(profile.getAddress());
        edtAge.setText(profile.getAge());
        for (HashTag item : profile.getInterestedHashtags()) {
            edtInterestedSubject.addObject(item);
        }
        edtSummary.setText(profile.getInfor());
        edtPhone.setText(profile.getPhoneNumber());
        spinnerGender.setSelection(profile.getGenderInt());
    }

}
