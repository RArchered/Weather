package com.example.weather.startpage;

import androidx.fragment.app.Fragment;

import com.example.weather.BaseView;

public class StartPageFragment extends Fragment implements StartPageContract.View {

    private StartPageContract.Persenter mPersenter;

    @Override
    public void setPresenter(StartPageContract.Persenter presenter) {
        mPersenter = presenter;
    }
}
