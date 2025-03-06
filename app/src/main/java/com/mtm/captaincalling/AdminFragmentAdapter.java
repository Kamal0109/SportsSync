package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminFragmentAdapter extends FragmentStateAdapter {

    public AdminFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        switch (i){
            case 0:
                return new AllBlogAdminActivity();
            case 1:
                return new AllBlogsPublicActivity();
            case 2:
                return new ProPlayerRequestsActivity();
            case 3:
                return new StaffRequestsActivity();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
