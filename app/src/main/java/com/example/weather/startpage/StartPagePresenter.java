package com.example.weather.startpage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.weather.data.TasksRepository;
import com.example.weather.data.db.TianqiCity;
import com.example.weather.data.db.TianqiLeader;
import com.example.weather.data.db.TianqiProvince;
import com.example.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.util.Preconditions.checkNotNull;

public class StartPagePresenter implements StartPageContract.Presenter {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_LEADER = 1;
    public static final int LEVEL_CITY = 2;
    //provinces
    private List<TianqiProvince> provinceList;
    //cities
    private List<TianqiLeader> leaderList;
    //counties
    private List<TianqiCity> cityList;

    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    //selected provincep
    private TianqiProvince selectedProvince;
    //selected city
    private TianqiLeader selectedLeader;
    //current level
    private int currentLevel;

    private StartPageContract.View mStartPageView;
    private TasksRepository mTasksRepository;

    public StartPagePresenter(@NonNull TasksRepository tasksRepository,
                              @NonNull StartPageContract.View startPageView) {
        mStartPageView = startPageView;
        mTasksRepository = tasksRepository;
        mStartPageView.setPresenter(this);
        adapter = new ArrayAdapter<>(mStartPageView.getActivityForView(),
                android.R.layout.simple_list_item_1, dataList);
    }

    public SharedPreferences getDefaultSharedPreferences(Context context) {
        return mTasksRepository.getDefaultSharedPreferences(context);
    }

    public void onBackPressed() {
        if (currentLevel == LEVEL_CITY) {
            queryLeaders();
        } else if (currentLevel == LEVEL_LEADER) {
            queryProvinces();
        }
    }

    public void queryProvinces() {
        mStartPageView.getTitleText().setText("中国");
        mStartPageView.getBackButton().setVisibility(View.GONE);
        provinceList = mTasksRepository.getProvinceList();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (TianqiProvince province : provinceList) {
                dataList.add(province.getProvinceZh());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        } else {
            //resolve city json at the first time
            mStartPageView.showProgressDialog();
            final Activity activity = mStartPageView.getActivityForView();
            //process in a new thread to avoid ANR
            new Thread(() -> {
                boolean result = Utility.handleTianqiCity(activity);
                if (result) {
                    activity.runOnUiThread(() -> {
                        mStartPageView.closeProgressDialog();
                        queryProvinces();
                    });
                } else {
                    //variable captured
                    //generate bridge method by compiler
                    activity.runOnUiThread(() -> {
                        mStartPageView.closeProgressDialog();
                        Toast.makeText(activity, "加载城市错误",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    public void queryLeaders() {
        mStartPageView.getTitleText().setText(selectedProvince.getProvinceZh());
        mStartPageView.getBackButton().setVisibility(View.VISIBLE);
        leaderList = mTasksRepository.getLeaderList(selectedProvince);
        if (leaderList.size() > 0) {
            dataList.clear();
            for (TianqiLeader leader : leaderList) {
                dataList.add(leader.getLeaderZh());
            }
            adapter.notifyDataSetChanged();
            mStartPageView.getListView().setSelection(0);
            currentLevel = LEVEL_LEADER;
        } else {
            Toast.makeText(mStartPageView.getActivityForView(), "加载城市错误", Toast.LENGTH_SHORT).show();
        }
    }

    public void queryCities() {
        mStartPageView.getTitleText().setText(selectedLeader.getLeaderZh());
        mStartPageView.getBackButton().setVisibility(View.VISIBLE);
        cityList = mTasksRepository.getCityList(selectedLeader);
        if (cityList.size() > 0) {
            dataList.clear();
            for (TianqiCity city : cityList) {
                dataList.add(city.getCityZh());
            }
            adapter.notifyDataSetChanged();
            mStartPageView.getListView().setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            Toast.makeText(mStartPageView.getActivityForView(), "加载城市错误", Toast.LENGTH_SHORT).show();
        }
    }


    public int getCurrentLevel() {
        return  currentLevel;
    }

    public TianqiProvince getSelectedProvince() {
        return selectedProvince;
    }

    public TianqiLeader getSelectedLeader() {
        return selectedLeader;
    }

    public List<TianqiProvince> getProvinceList() {
        return provinceList;
    }

    public List<TianqiLeader> getLeaderList() {
        return leaderList;
    }

    public List<TianqiCity> getCityList() {
        return cityList;
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public void setSelectedProvince(TianqiProvince selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

    public void setSelectedLeader(TianqiLeader selectedLeader) {
        this.selectedLeader = selectedLeader;
    }

    public List<String> getDataList() {
        return dataList;
    }
}
