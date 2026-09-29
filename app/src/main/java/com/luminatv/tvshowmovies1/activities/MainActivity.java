package com.luminatv.tvshowmovies1.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.applovin.sdk.AppLovinEventTypes;
import com.luminatv.tvshowmovies1.R;
import com.luminatv.tvshowmovies1.activities.ads.AppLAds;
import com.luminatv.tvshowmovies1.fragments.LibraryFragment;
import com.luminatv.tvshowmovies1.fragments.MoviesFragment;
import com.luminatv.tvshowmovies1.fragments.SearchFragment;
import com.luminatv.tvshowmovies1.fragments.TvShowsFragment;
import com.luminatv.tvshowmovies1.utils.UpdateChecker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationBarView;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends AppCompatActivity {
    private Fragment activeFragment;
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigation;
    private Fragment libraryFragment;
    private Toolbar mainToolbar;
    private Fragment moviesFragment;
    private Fragment searchFragment;
    private Fragment tvShowsFragment;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark, getTheme()));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface_dark, getTheme()));
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.drawerNavigation = (NavigationView) findViewById(R.id.drawerNavigation);
        this.mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        this.bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        this.bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        AppLAds.INSTANCE.refreshAdConfig(this, new Runnable() { // from class: com.freewatching.magistv4.activities.MainActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.m293xd03c96b2();
            }
        });
        this.moviesFragment = new MoviesFragment();
        this.tvShowsFragment = new TvShowsFragment();
        this.searchFragment = new SearchFragment();
        this.libraryFragment = new LibraryFragment();
        this.activeFragment = this.moviesFragment;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, this.libraryFragment, "library").hide(this.libraryFragment)
                .add(R.id.fragmentContainer, this.searchFragment, AppLovinEventTypes.USER_EXECUTED_SEARCH).hide(this.searchFragment)
                .add(R.id.fragmentContainer, this.tvShowsFragment, "tvshows").hide(this.tvShowsFragment)
                .add(R.id.fragmentContainer, this.moviesFragment, "movies")
                .commit();
        this.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() { // from class: com.freewatching.magistv4.activities.MainActivity$$ExternalSyntheticLambda1
            @Override // com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
            public final boolean onNavigationItemSelected(MenuItem menuItem) {
                return MainActivity.this.m294x13c7b473(menuItem);
            }
        });
        this.bottomNavigation.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem menuItem) {
            }
        });
        setupDrawer();
        setupBackPressHandler();
        UpdateChecker.checkForUpdate(this);
    }

    /* JADX INFO: renamed from: lambda$onCreate$0$com-freewatching-magistv4-activities-MainActivity, reason: not valid java name */
    /* synthetic */ void m293xd03c96b2() {
        AppLAds.INSTANCE.initializeSDK(this, new Runnable() {
            @Override
            public void run() {
                AppLAds.INSTANCE.initializeAds(MainActivity.this);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onCreate$1$com-freewatching-magistv4-activities-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m294x13c7b473(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_movies) {
            switchFragment(this.moviesFragment);
            return true;
        }
        if (itemId == R.id.nav_tvshows) {
            switchFragment(this.tvShowsFragment);
            return true;
        }
        if (itemId != R.id.nav_search) {
            if (itemId != R.id.nav_library) {
                return false;
            }
            switchFragment(this.libraryFragment);
            return true;
        }
        switchFragment(this.searchFragment);
        return true;
    }

    private void switchFragment(Fragment fragment) {
        if (fragment != this.activeFragment) {
            getSupportFragmentManager().beginTransaction().hide(this.activeFragment).show(fragment).commit();
            this.activeFragment = fragment;
        }
    }

    private void setupDrawer() {
        this.mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        this.drawerNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                int itemId = item.getItemId();
                if (itemId == R.id.drawer_rate) {
                    openRatePage();
                    return true;
                }
                if (itemId == R.id.drawer_privacy) {
                    showInfoDialog(getString(R.string.privacy_policy_title), getString(R.string.privacy_policy_message));
                    return true;
                }
                if (itemId == R.id.drawer_contact) {
                    openContactUs();
                    return true;
                }
                if (itemId == R.id.drawer_about) {
                    showInfoDialog(getString(R.string.about_title), getString(R.string.about_message));
                    return true;
                }
                return false;
            }
        });
    }

    private void openRatePage() {
        String packageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    private void openContactUs() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + getString(R.string.contact_email)));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " support");
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.drawer_contact_us)));
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, getString(R.string.contact_email), Toast.LENGTH_LONG).show();
        }
    }

    private void showInfoDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        AppLAds.INSTANCE.clearCurrentActivity(this);
        super.onDestroy();
    }
}
