package com.fray.launcher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ray on 03.08.2014.
 */
public class ButtonsFragment extends Fragment {

    private static String TAG = "ButtonsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.buttonsfragment, container, false);

        return v;
    }

}
