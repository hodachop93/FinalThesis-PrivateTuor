package com.hodachop93.hohoda.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;

/**
 * Created by Hop on 13/03/2016.
 */
public class BaseFragment extends Fragment {


    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /*
    * Called when the fragment attaches to the context
    */
    protected void onAttachToContext(Context context) {
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
