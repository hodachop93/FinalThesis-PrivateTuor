package com.hodachop93.hohoda.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.api.usermanagement.UserManagementApi;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.common.ApplicationConstants;
import com.hodachop93.hohoda.eventbus.SearchEventBus;
import com.hodachop93.hohoda.fragment.BrowseCourseFragment;
import com.hodachop93.hohoda.fragment.BrowseTutorFragment;
import com.hodachop93.hohoda.fragment.ConversationListFragment;
import com.hodachop93.hohoda.fragment.DashboardFragment;
import com.hodachop93.hohoda.fragment.FragmentNavigationDrawer;
import com.hodachop93.hohoda.fragment.MyCourseFragment;
import com.hodachop93.hohoda.fragment.NotificationListFragment;
import com.hodachop93.hohoda.fragment.PostCourseFragment;
import com.hodachop93.hohoda.fragment.SearchCoursesFragment;
import com.hodachop93.hohoda.fragment.SearchTutorsFragment;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.utils.HashtagUtils;
import com.hodachop93.hohoda.utils.NotificationUtils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class BaseNavigationDrawerActivity extends BaseActivity implements FragmentNavigationDrawer.FragmentNavigationDrawerListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private FragmentNavigationDrawer navDrawerFragment;

    //List Fragment for Menu
    private MyCourseFragment mMyCourseFragment;
    private PostCourseFragment mPostCourseFragment;
    private BrowseCourseFragment mBrowseCourseFragment;
    private BrowseTutorFragment mBrowseTutorFragment;
    private ConversationListFragment mConversationListFragment;
    private NotificationListFragment mNotificationListFragment;
    private SearchCoursesFragment mSearchCoursesFragment;
    private SearchTutorsFragment mSearchTutorsFragment;
    private DashboardFragment mDashboardFragment;

    private MaterialSearchView searchView;

    private String mSearchQuery;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hohoda);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        navDrawerFragment = (FragmentNavigationDrawer) getFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        navDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        navDrawerFragment.setDrawerListener(this);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchQuery = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                handleActionSearch();
            }
        });


        List<String> hashTags = HashtagUtils.getNamePopularHashtags();
        if (hashTags != null)
            searchView.setSuggestions(hashTags.toArray(new String[hashTags.size()]));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_activity_hohoda, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        if (mPosition == ApplicationConstants.NavigationMenuPosition.BROWSE_COURSES ||
                mPosition == ApplicationConstants.NavigationMenuPosition.BROWSE_TUTORS) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void handleActionSearch() {
        if (mSearchQuery == null || mSearchQuery.isEmpty())
            return;

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment == null)
            return;

        ArrayList<String> hashtags = splitSearchQuery(mSearchQuery);
        if (hashtags == null)
            return;

        if (currentFragment.getClass().getSimpleName().equals("BrowseCourseFragment")) {
            switchToSearchCoursesFragment(hashtags);
        }
        if (currentFragment.getClass().getSimpleName().equals("BrowseTutorFragment")) {
            switchToSearchTutorsFragment(hashtags);
        }
        if (currentFragment.getClass().getSimpleName().equals("SearchCoursesFragment")) {
            SearchEventBus.getInstance().postSticky(new SearchEventBus.Event(hashtags));
        }
        if (currentFragment.getClass().getSimpleName().equals("SearchTutorsFragment")) {
            SearchEventBus.getInstance().postSticky(new SearchEventBus.Event(hashtags));
        }
        mSearchQuery = null;
    }

    private ArrayList<String> splitSearchQuery(@NonNull String searchQuery) {
        String[] hashtags = null;
        if (searchQuery.contains(",")) {
            hashtags = searchQuery.split(",");
        } else if (searchQuery.contains(" ")) {
            hashtags = searchQuery.split(" ");
        }

        ArrayList<String> result = new ArrayList<>();
        if (hashtags == null || hashtags.length == 0) {
            result.add(searchQuery);
        } else {
            for (String item : hashtags) {
                item.trim();
            }

            for (String item : hashtags) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void OnMenuItemSelected(String title, int position) {
        getSupportActionBar().setTitle(title);
        mPosition = position;
        invalidateOptionsMenu();

        switch (position) {
            case ApplicationConstants.NavigationMenuPosition.DASHBOARD:
                switchToDashboardFragment();
                break;
            case ApplicationConstants.NavigationMenuPosition.NOTIFICATIONS:
                switchToNotificationsFragment();
                break;
            case ApplicationConstants.NavigationMenuPosition.MESSAGES:
                switchToConversationListFragment();
                break;
            case ApplicationConstants.NavigationMenuPosition.MY_COURSES:
                switchToMyCourseFragment();
                break;
            case ApplicationConstants.NavigationMenuPosition.POST_COURSE:
                switchToPostCourseFragment();
                break;
            case ApplicationConstants.NavigationMenuPosition.BROWSE_COURSES:
                switchToBrowseCoursesFragment();
                break;
            case ApplicationConstants.NavigationMenuPosition.BROWSE_TUTORS:
                switchToBrowseTutorsFragment();
                break;
            case ApplicationConstants.NavigationMenuPosition.SIGN_OUT:
                signOut();
                break;
        }
    }


    private void switchToSearchTutorsFragment(ArrayList<String> hashtags) {
        if (mSearchTutorsFragment == null)
            mSearchTutorsFragment = SearchTutorsFragment.newInstance();
        replaceFragment(mSearchTutorsFragment);
        SearchEventBus.getInstance().postSticky(new SearchEventBus.Event(hashtags));
    }

    private void switchToSearchCoursesFragment(ArrayList<String> hashtags) {
        if (mSearchCoursesFragment == null)
            mSearchCoursesFragment = SearchCoursesFragment.newInstance();
        replaceFragment(mSearchCoursesFragment);
        SearchEventBus.getInstance().postSticky(new SearchEventBus.Event(hashtags));
    }

    private void switchToBrowseTutorsFragment() {
        if (mBrowseTutorFragment == null) {
            mBrowseTutorFragment = BrowseTutorFragment.newInstance();
        }
        replaceFragment(mBrowseTutorFragment);
    }

    private void switchToBrowseCoursesFragment() {
        if (mBrowseCourseFragment == null)
            mBrowseCourseFragment = BrowseCourseFragment.newInstance();
        replaceFragment(mBrowseCourseFragment);
    }

    private void switchToMyCourseFragment() {
        if (mMyCourseFragment == null)
            mMyCourseFragment = MyCourseFragment.newInstance();
        replaceFragment(mMyCourseFragment);
    }

    private void switchToNotificationsFragment() {
        if (mNotificationListFragment == null) {
            mNotificationListFragment = NotificationListFragment.newInstance();
        }
        replaceFragment(mNotificationListFragment);
    }

    private void switchToPostCourseFragment() {
        if (mPostCourseFragment == null)
            mPostCourseFragment = PostCourseFragment.newInstance();
        replaceFragment(mPostCourseFragment);
    }

    private void switchToConversationListFragment() {
        if (mConversationListFragment == null)
            mConversationListFragment = ConversationListFragment.newInstance();
        replaceFragment(mConversationListFragment);
    }

    protected void switchToDashboardFragment() {
        if (mDashboardFragment == null) {
            mDashboardFragment = DashboardFragment.newInstance();
        }
        replaceFragment(mDashboardFragment);
    }

    private void signOut() {
        Callback<HohodaResponse<Object>> callback = new Callback<HohodaResponse<Object>>() {
            @Override
            public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {
                AppReferences.setUserTokenID(null);
                AppReferences.setUserID(null);
                NotificationUtils.clearAllNotifications();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(SignInActivity.getIntent(BaseNavigationDrawerActivity.this));
            }

            @Override
            public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {

            }
        };

        Call<HohodaResponse<Object>> call = UserManagementApi.getInstance().signOut();
        call.enqueue(callback);
    }

    private void replaceFragment(Fragment fragment) {

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);

        // Ignore replace fragment if it is showing on screen
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }

        String backStateName = fragment.getClass().getName();

        // Re-use fragment if it was added to back-stack
        FragmentManager manager = getFragmentManager();

        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
//            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
            ft.replace(R.id.fragment_container, fragment);

            if (currentFragment != null)
                ft.hide(currentFragment);

            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
