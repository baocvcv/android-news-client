package com.java.baohan.FragmentInterface.EntityInterface;

import androidx.fragment.app.Fragment;

//Fragment for Epidemic map
public class FragmentInterface3 extends Fragment {

    private static FragmentInterface3 INSTANCE = null;

    private FragmentInterface3() {}

    public static FragmentInterface3 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FragmentInterface3();
        }
        return INSTANCE;
    }
}
