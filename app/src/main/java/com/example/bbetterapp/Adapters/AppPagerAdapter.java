package com.example.bbetterapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bbetterapp.Fragments.CalendarFragment;
import com.example.bbetterapp.Fragments.HabitTrackerFragment;
import com.example.bbetterapp.Fragments.NotesFragment;
import com.example.bbetterapp.Fragments.TimerFragment;
import com.example.bbetterapp.Fragments.ToDoFragment;

public class AppPagerAdapter extends FragmentStateAdapter {

    public AppPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ToDoFragment();
            case 1:
                return new CalendarFragment();
            case 2:
                return new TimerFragment();
            default:
                return new NotesFragment();
            /*default:
                return new HabitTrackerFragment();*/
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
