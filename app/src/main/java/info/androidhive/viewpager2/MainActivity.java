package info.androidhive.viewpager2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.view.View;

import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayoutMediator;

import info.androidhive.viewpager2.databinding.ActivityFragmentViewPagerBinding;
import info.androidhive.viewpager2.fragments.Screen1;
import info.androidhive.viewpager2.fragments.Screen2;
import info.androidhive.viewpager2.fragments.Screen3;
import info.androidhive.viewpager2.fragments.Screen4;

public class MainActivity extends AppCompatActivity {

    public static RequestHelper request;
    ActivityFragmentViewPagerBinding binding;
    String[] tabTitles = new String[]{"\u278A", "\u278B", "\u278C", "\u278D"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentViewPagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // static request object
        request = new RequestHelper(Volley.newRequestQueue(this), this, null, null);

        // removing toolbar elevation
        getSupportActionBar().setElevation(0);

        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(this));

        // attaching tab mediator
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    /**
     * ViewPagerFragmentAdapter
     *
     * This class instanciates our different screens when we slide from left to right
     * Each time, a new screen is invoked and the last screen is killed
     */
    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Screen1();
                case 1:
                    return new Screen2();
                case 2:
                    return new Screen3();
                case 3:
                    return new Screen4();
            }
            return new Screen1();
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }


    /**
     * setImmersiveMode()
     *
     * Hides the ugly system bars at the top and bottom of the application
     */
    public void setImmersiveMode() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     * onWindowFocusChanged()
     *
     * When we leave the BlueBox app and come back to it, the ugly system bars are back again !
     * Hence, we need to kill them once more
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setImmersiveMode();
        }
    }
}
