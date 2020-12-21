package com.gopal.onboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.gopal.ebookapp.MainActivity;
import com.gopal.ebookapp.R;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    private IntroViewPagerAdapter introViewPagerAdapter;
    private TabLayout tabIndicator;
    private Button nextBtn;
    private int position = 0;
    private Animation btnAnimation;
    private Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        //when this activity is about to launch check shared preference
        if (restoredPrefData()) {
            startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
            finish();
        }

        setContentView( R.layout.activity_intro );

        tabIndicator = findViewById( R.id.tab_indicator );
        nextBtn = findViewById( R.id.nextBtn );
        getStarted = findViewById( R.id.getStartedBtn );
        btnAnimation = AnimationUtils.loadAnimation( getApplicationContext(), R.anim.button_animation );

        //filling screen

        final List<ScreenItem> list = new ArrayList<>();

        list.add( new ScreenItem( "Welcome To BookHolic", "Find, get, and share books you love on BookHolic.", R.drawable.whitelogo ) );
        list.add( new ScreenItem( "Read Books On the Go", "Read From the vast Collection of books for free. Read books anywhere , Anytime. Create your Library of your favorite books", R.drawable.gopalis ) );
        list.add( new ScreenItem( "Private Books", "Add your Book Privately without Sharing. Make the private library of your books , Pdf file ", R.drawable.privatee ) );
        list.add( new ScreenItem( "Track Your Progress", "Make yourself free from remembering last page you read every time. Let us do your this job .You can Track the Progress of all the Read books. Auto Continuation of last Page You Read.", R.drawable.trackk ) );
        list.add( new ScreenItem( "Want to Know More? Read More? Expand your thinking?", "Sign In And explore the BookHolic now :)", R.drawable.knowmore ) );

        //setting up viewPager
        screenPager = findViewById( R.id.screen_viewPager );
        introViewPagerAdapter = new IntroViewPagerAdapter( this, list );
        screenPager.setAdapter( introViewPagerAdapter );

        //Setting up tab Layout
        tabIndicator.setupWithViewPager( screenPager );
        nextBtn.setOnClickListener( v -> {

            position = screenPager.getCurrentItem();
            if (position < list.size()) {

                position++;
                screenPager.setCurrentItem( position );
            }

            if (position == list.size() - 1) {
                loadLastScreen();
            }

        } );

        tabIndicator.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == list.size() - 1) {
                    screenPager.beginFakeDrag();
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );

        getStarted.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                startActivity( intent );
                finish();
                savePreference();
            }
        } );

    }

    private boolean restoredPrefData() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences( "myPref", MODE_PRIVATE );
        return sharedPreferences.getBoolean( "isIntroOpened", false );
    }

    private void savePreference() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences( "myPref", MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean( "isIntroOpened", true );
        editor.apply();
    }

    private void loadLastScreen() {

        nextBtn.setVisibility( View.INVISIBLE );
        tabIndicator.setVisibility( View.INVISIBLE );
        getStarted.setAnimation( btnAnimation );
//        screenPager.beginFakeDrag();
        getStarted.setVisibility( View.VISIBLE );

    }
}
