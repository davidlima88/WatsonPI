package davidlima.watsonpi.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import davidlima.watsonpi.fragments.NeedsFragment;
import davidlima.watsonpi.fragments.PersonalityFragment;
import davidlima.watsonpi.fragments.SummaryFragment;
import davidlima.watsonpi.fragments.ValuesFragment;

public class BottomBarAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public BottomBarAdapter(FragmentManager fm, String json) {
        super(fm);

        fragments.clear();
        fragments.add(SummaryFragment.newInstance(json));
        fragments.add(PersonalityFragment.newInstance(json));
        fragments.add(NeedsFragment.newInstance(json));
        fragments.add(ValuesFragment.newInstance(json));
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}
