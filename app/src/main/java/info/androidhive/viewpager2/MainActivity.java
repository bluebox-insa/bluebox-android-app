package info.androidhive.viewpager2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import info.androidhive.viewpager2.databinding.ActivityMainBinding;
import info.androidhive.viewpager2.fragments.FragmentViewPagerActivity;
import info.androidhive.viewpager2.transformers.CubeInDepthTransformation;
import info.androidhive.viewpager2.transformers.DepthPageTransformer;
import info.androidhive.viewpager2.transformers.GateTransformation;
import info.androidhive.viewpager2.transformers.TossTransformation;
import info.androidhive.viewpager2.transformers.ZoomOutPageTransformer;
import info.androidhive.viewpager2.views.ViewsSliderActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*          BEFORE       */
        /*  Create a view with two buttons, each button launches an activity     */
//        binding.btnViewsDemo.setOnClickListener(view -> {
//            startActivity(new Intent(MainActivity.this, ViewsSliderActivity.class));
//        });
//        binding.btnFragmentDemo.setOnClickListener(view -> {
//            startActivity(new Intent(MainActivity.this, FragmentViewPagerActivity.class));
//        });


        /*          AFTER      */
        /*  Launch the fragment activity by default     */
        startActivity(new Intent(MainActivity.this, FragmentViewPagerActivity.class));
    }
}