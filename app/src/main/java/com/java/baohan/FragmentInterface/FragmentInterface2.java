package com.java.baohan.FragmentInterface;

import androidx.fragment.app.Fragment;

//Fragment for Epidemic data
public class FragmentInterface2 extends Fragment {

    private static FragmentInterface2 INSTANCE = null;

    private FragmentInterface2() {}

    public static FragmentInterface2 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FragmentInterface2();
        }
        return INSTANCE;
    }
}
