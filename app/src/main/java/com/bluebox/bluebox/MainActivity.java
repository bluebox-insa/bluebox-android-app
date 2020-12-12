package com.bluebox.bluebox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.bluebox.bluebox.databinding.ActivityMainBinding;
import com.bluebox.bluebox.fragments.Screen1;
import com.bluebox.bluebox.fragments.Screen2;
import com.bluebox.bluebox.fragments.Screen3;
import com.bluebox.bluebox.fragments.Screen4;

public class MainActivity extends AppCompatActivity {

    public static RequestHelper request;
    ActivityMainBinding binding;

    String[] tabTitles = new String[]{"\u25CB", "\u25CB", "\u25CB", "\u25CB"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide status bar on launch
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){
            Log.e("OnCreate", "Status bar not found");
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // static request object
        request = new RequestHelper(Volley.newRequestQueue(this), this);

        // removing toolbar elevation
        getSupportActionBar().setElevation(0);

        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(this));

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = binding.tabLayout.getSelectedTabPosition();
                if (currentPosition < 3) {
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(currentPosition+1));
                } else {
                    MainActivity.this.finish();
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = binding.tabLayout.getSelectedTabPosition();
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(currentPosition-1));
            }
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setText("\u25CF");
                switch (tab.getPosition()) {
                    case 0:
                        binding.back.setVisibility(View.INVISIBLE);
                        binding.tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_slide_1));
                        break;
                    case 1:
                        binding.back.setVisibility(View.VISIBLE);
                        binding.tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_slide_2));
                        break;
                    case 2:
                        binding.back.setVisibility(View.VISIBLE);
                        binding.tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_slide_3));
                        binding.next.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_next));
                        break;
                    case 3:
                        binding.back.setVisibility(View.INVISIBLE);
                        binding.tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.back_slide_4));
                        binding.next.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_checkmark));
                        break;
                    default:
                        Log.e("onTabSelected", "tab.getPosition() is not include in [0:3] as expected.");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setText("\u25CB");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
