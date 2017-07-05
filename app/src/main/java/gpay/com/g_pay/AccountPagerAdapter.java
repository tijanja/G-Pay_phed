package gpay.com.g_pay;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adetunji on 6/13/17.
 */

class AccountPagerAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<String> itemList;
    private List<Object> sectionTextHash;
    private String email,authToken;
    private CoordinatorLayout mainCoordinator;

    public AccountPagerAdapter(FragmentManager fm, List<Object> sectHash, String email__, String __authToken, CoordinatorLayout coord)
    {
        super(fm);
        sectionTextHash = sectHash;
        email = email__;
        authToken = __authToken;
        mainCoordinator = coord;
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment= AccountFragment.newInstance((AccountMeter) sectionTextHash.get(position),email,authToken,mainCoordinator);
        return fragment;
    }

    @Override
    public int getCount() {
        return sectionTextHash.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        AccountMeter section = (AccountMeter)sectionTextHash.get(position);
        return "";//section.getSectionTitle();

    }


}