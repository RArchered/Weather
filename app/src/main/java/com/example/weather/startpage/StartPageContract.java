package com.example.weather.startpage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weather.BasePresenter;
import com.example.weather.BaseView;
import com.example.weather.data.db.TianqiCity;
import com.example.weather.data.db.TianqiLeader;
import com.example.weather.data.db.TianqiProvince;

import java.util.List;

public interface StartPageContract {
    interface View extends BaseView<Presenter> {
        public Activity getActivityForView();
        public void showProgressDialog();
        public void closeProgressDialog();

        public TextView getTitleText();

        public Button getBackButton();

        public ListView getListView();
    }

    interface Presenter extends BasePresenter {
        public int getCurrentLevel();
        public TianqiProvince getSelectedProvince();

        public TianqiLeader getSelectedLeader();

        public List<TianqiProvince> getProvinceList();

        public List<TianqiLeader> getLeaderList();

        public List<TianqiCity> getCityList();

        public List<String> getDataList();

        public ArrayAdapter<String> getAdapter();

        public void setSelectedProvince(TianqiProvince selectedProvince);

        public void setSelectedLeader(TianqiLeader selectedLeader);

        public void queryProvinces();

        public void queryLeaders();

        public void queryCities();

        public SharedPreferences getDefaultSharedPreferences(Context context);
    }
}
