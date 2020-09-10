package com.java.baohan.FragmentInterface;

import androidx.fragment.app.Fragment;

//Fragment for relative scholar
public class FragmentInterface4 extends Fragment {

    private static FragmentInterface4 INSTANCE = null;

    private FragmentInterface4() {}

    public static FragmentInterface4 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FragmentInterface4();
        }
        return INSTANCE;
    }
}
