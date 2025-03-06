package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HirePeopleFragmentAdapter extends FragmentStateAdapter {

    public HirePeopleFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        switch (i){
            case 0:
                return new HireProPlayerActivity();
            case 1:
                return new HireStaffActivity();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
