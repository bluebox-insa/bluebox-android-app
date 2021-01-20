package com.bluebox.bluebox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import com.android.volley.toolbox.Volley;
import com.bluebox.bluebox.screens.Screen1;
import com.bluebox.bluebox.utils.Logger;
import com.bluebox.bluebox.utils.Requests;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.bluebox.bluebox.databinding.ActivityMainBinding;
import com.bluebox.bluebox.screens.Screen0;
import com.bluebox.bluebox.screens.Screen2;
import com.bluebox.bluebox.screens.Screen3;
import com.bluebox.bluebox.screens.Screen4;

public class MainActivity extends AppCompatActivity {

    // (!) bit of a hack here
    // \u25CB is the Unicode character for an empty circle
    // we set all tab titles to empty circles, and then when we select a tab we change its title to a filled circle
    public final static String[] tabTitles = new String[]{"\u25CB", "\u25CB", "\u25CB", "\u25CB", "\u25CB"};

    public static Requests requests;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide status bar on launch
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){
            Logger.e("Could not hide Android status bar: "+e);
        }

        // removing toolbar elevation
        getSupportActionBar().setElevation(0);

        // attaching our FragmentAdapter to the MainActivity: the main logic is done in FragmentAdapter
        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(this));

        // => next screen button
        binding.nextButton.setOnClickListener((View v1) -> {
                int currentPosition = binding.tabLayout.getSelectedTabPosition();
                if (currentPosition < tabTitles.length-1) {
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(currentPosition+1));
                } else {
                    MainActivity.this.finish();
                }
        });

        // <= previous screen button
        binding.backButton.setOnClickListener((View v2) -> {
                int currentPosition = binding.tabLayout.getSelectedTabPosition();
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(currentPosition-1));
        });

        binding.tabLayout.addOnTabSelectedListener(new CustomOnTabSelectedListener());

        // attach tab mediator
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();

        // initialize static Requests object
        requests = new Requests(Volley.newRequestQueue(this), this);
    }

    /**
     * OnTabSelectedListener()
     *
     * Set buttons visible or invisible depending on the screen (e.g. on first screen there should not be a "<= return" button)
     * And change the background of the bottom tab layout
     */
    private class CustomOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.setText("\u25CF");

            switch (tab.getPosition()) {
                case 0:
                case 1:
                    binding.backButton.setVisibility(View.INVISIBLE);
                    binding.tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_slide_1));
                    break;
                case 2:
                    binding.backButton.setVisibility(View.VISIBLE);
                    binding.tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_slide_2));
                    break;
                case 3:
                    binding.backButton.setVisibility(View.VISIBLE);
                    binding.tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_slide_3));
                    binding.nextButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_next));
                    break;
                case 4:
                    binding.backButton.setVisibility(View.INVISIBLE);
                    binding.tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_slide_4));
                    binding.nextButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_checkmark));
                    break;
                default:
                    Logger.e("tab.getPosition() is not include in [0:4] as expected.");
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            tab.setText("\u25CB");
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
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
                    return new Screen0();
                case 1:
                    return new Screen1();
                case 2:
                    return new Screen2();
//                    return new FakeScreen2();
                case 3:
                    return new Screen3();
//                    return new FakeScreen3();
                case 4:
                    return new Screen4();
            }
            return new Screen0();
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }


    /**
     * setImmersiveMode() to hide navigation
     */
    public void setImmersiveMode() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                        // View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show
                        //  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        // View.SYSTEM_UI_FLAG_FULLSCREEN);
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
