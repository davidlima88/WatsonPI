package davidlima.watsonpi.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import davidlima.watsonpi.adapters.BottomBarAdapter;
import davidlima.watsonpi.R;

public class ResponseActivity extends AppCompatActivity {

    private AHBottomNavigationViewPager viewPager;
    private AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String json;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                json = null;
            } else {
                json = extras.getString(MainActivity.JSON_RESPONSE);
            }
        } else {
            json = (String) savedInstanceState.getSerializable(MainActivity.JSON_RESPONSE);
        }

        setContentView(R.layout.activity_response);
        setupViewPager(json);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        addBottomNavigationItems();
        setupBottomNavStyle();
        bottomNavigation.setOnTabSelectedListener(onTabSelectedListener);
        bottomNavigation.setCurrentItem(0);
    }

    private void setupViewPager(String json) {
        viewPager = findViewById(R.id.view_pager);
        viewPager.setPagingEnabled(false);
        BottomBarAdapter pagerAdapter = new BottomBarAdapter(getSupportFragmentManager(), json);
        viewPager.setAdapter(pagerAdapter);
    }

    private void addBottomNavigationItems() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.menu_summary, R.drawable.ic_summary_black_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.menu_personality, R.drawable.ic_personality_black_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.menu_needs, R.drawable.ic_needs_black_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.menu_values, R.drawable.ic_values_black_24dp, R.color.colorPrimary);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
    }

    private void setupBottomNavStyle() {
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(R.color.colorBottomNavigationAccent);
        bottomNavigation.setInactiveColor(R.color.colorBottomNavigationInactiveColored);

        bottomNavigation.setColoredModeColors(Color.WHITE, Color.LTGRAY);

        bottomNavigation.setColored(true);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        bottomNavigation.setTranslucentNavigationEnabled(true);
    }

    private AHBottomNavigation.OnTabSelectedListener onTabSelectedListener
            = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            if (!wasSelected)
                viewPager.setCurrentItem(position);
            return true;
        }
    };
}
