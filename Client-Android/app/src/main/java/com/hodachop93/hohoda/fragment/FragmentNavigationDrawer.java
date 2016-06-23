package com.hodachop93.hohoda.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.activity.UserProfileActivity;
import com.hodachop93.hohoda.adapter.NavigationDrawerAdapter;
import com.hodachop93.hohoda.common.ApplicationConstants;
import com.hodachop93.hohoda.eventbus.ProfileEventBus;
import com.hodachop93.hohoda.model.NavigationDrawerItem;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.utils.ProfileUtils;
import com.hodachop93.hohoda.utils.Utils;
import com.hodachop93.hohoda.view.CircleImageView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hop on 04/03/2016.
 */
public class FragmentNavigationDrawer extends Fragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.drawer_list)
    ListView drawerList;
    @Bind(R.id.imv_avatar)
    CircleImageView imvAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_email)
    TextView tvEmail;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter mNavDrawerAdapter;
    private View containerView;

    private FragmentNavigationDrawerListener mDrawerListener;

    public FragmentNavigationDrawer() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.bind(this, layout);
        mNavDrawerAdapter = new NavigationDrawerAdapter(getActivity());
        drawerList.setAdapter(mNavDrawerAdapter);
        drawerList.setOnItemClickListener(this);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        ProfileEventBus.getInstance().register(this);
    }

    @Override
    public void onStop() {
        ProfileEventBus.getInstance().unregister(this);
        super.onStop();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onProfileEvent(ProfileEventBus.UpdateProfileEvent updateProfileEvent) {
        Profile profile = updateProfileEvent.getProfile();
        if (profile == null)
            return;

        Picasso.with(getActivity()).load(Utils.convertUrlIfUsingLocalhost(profile.getAvatarUrl()))
                .placeholder(ContextCompat.getDrawable(getActivity(), R.drawable.ic_user_default))
                .into(imvAvatar);
        tvName.setText(profile.getName());
        tvEmail.setText(profile.getEmail());

        NavigationDrawerItem item = mNavDrawerAdapter.getNavigationDrawerItemAtPosition(ApplicationConstants.NavigationMenuPosition.NOTIFICATIONS);
        if (item != null) {
            int unreadNotifications = profile.getTotalUnreadNotification();
            item.setBadge(unreadNotifications);
            mNavDrawerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //Set up navigation drawer layout
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.nav_header_container)
    public void showUserProfile() {
        Intent intent = UserProfileActivity.getIntent(getActivity());
        startActivity(intent);
    }

    //When click on a navigation drawer menu item
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mDrawerListener != null) {
            mDrawerLayout.closeDrawer(containerView);
            mDrawerListener.OnMenuItemSelected(mNavDrawerAdapter.getTitleSelectedItem(position), position);
        }
    }

    public void setDrawerListener(FragmentNavigationDrawerListener listener) {
        this.mDrawerListener = listener;
    }

    public interface FragmentNavigationDrawerListener {
        public void OnMenuItemSelected(String title, int position);
    }

}
