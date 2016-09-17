package com.smartcave.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by USER on 25-05-2016.
 */
public class BaseFragment extends Fragment {

    private int mLayout;
    private View mRootView;

    protected void setContentView(int layout){
        mLayout = layout;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(mLayout, container, false);
        return mRootView;
    }

    protected View findViewById(int id){
        return mRootView.findViewById(id);
    }
}
