package com.example.weather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.weather.db.TianqiCity;
import com.example.weather.db.TianqiLeader;
import com.example.weather.db.TianqiProvince;
import com.example.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_LEADER = 1;
    public static final int LEVEL_CITY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    //provinces
    private List<TianqiProvince> provinceList;
    //cities
    private List<TianqiLeader> leaderList;
    //counties
    private List<TianqiCity> cityList;
    //selected provincep
    private TianqiProvince selectedProvince;
    //selected city
    private TianqiLeader selectedLeader;
    //current level
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {
                    if (currentLevel == LEVEL_PROVINCE) {
                        selectedProvince = provinceList.get(position);
                        queryLeaders();
                    } else if (currentLevel == LEVEL_LEADER) {
                        selectedLeader = leaderList.get(position);
                        queryCities();
                    } else if (currentLevel == LEVEL_CITY) {
                        String cityId = cityList.get(position).getCityId();
                        if (getActivity() instanceof MainActivity) {
                            Intent intent = new Intent(getActivity(), WeatherActivity.class);
                            intent.putExtra("city_id", cityId);
                            startActivity(intent);
                            getActivity().finish();
                        } else if (getActivity() instanceof WeatherActivity) {
                            WeatherActivity activity = (WeatherActivity) getActivity();
                            activity.drawerLayout.closeDrawers();
                            activity.swipeRefresh.setRefreshing(true);
                            activity.requestWeather(cityId);
                        }
                    }
                });
        backButton.setOnClickListener(v -> {
                    if (currentLevel == LEVEL_CITY) {
                        queryLeaders();
                    } else if (currentLevel == LEVEL_LEADER) {
                        queryProvinces();
                    }
                });
        queryProvinces();
    }

    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(TianqiProvince.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (TianqiProvince province : provinceList) {
                dataList.add(province.getProvinceZh());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        } else {
            //resolve city json at the first time
            showProgressDialog();
            final Activity activity = getActivity();
            new Thread(() -> {
               boolean result = Utility.handleTianqiCity(activity);
               if (result) {
                   activity.runOnUiThread(() -> {
                      closeProgressDialog();
                      queryProvinces();
                   });
               } else {
                   activity.runOnUiThread(() -> {
                       closeProgressDialog();
                       Toast.makeText(activity, "加载城市错误",
                               Toast.LENGTH_SHORT).show();
                   });
               }
            }).start();
        }
    }

    private void queryLeaders() {
        titleText.setText(selectedProvince.getProvinceZh());
        backButton.setVisibility(View.VISIBLE);
        leaderList = DataSupport.where("provinceZh = ?",
                String.valueOf(selectedProvince.getProvinceZh())).find(TianqiLeader.class);
        if (leaderList.size() > 0) {
            dataList.clear();
            for (TianqiLeader leader : leaderList) {
                dataList.add(leader.getLeaderZh());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_LEADER;
        } else {
            Toast.makeText(getActivity(), "加载城市错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void queryCities() {
        titleText.setText(selectedLeader.getLeaderZh());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("leaderZh = ?",
                String.valueOf(selectedLeader.getLeaderZh())).find(TianqiCity.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (TianqiCity city : cityList) {
                dataList.add(city.getCityZh());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            Toast.makeText(getActivity(), "加载城市错误", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        if (currentLevel == LEVEL_CITY) {
            queryLeaders();
        } else if (currentLevel == LEVEL_LEADER) {
            queryProvinces();
        }
    }

    public int getCurrentLevel() {
        return  currentLevel;
    }

    /*private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    closeProgressDialog();
                    Toast.makeText(getContext(), "加载失败",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText,
                            selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText,
                            selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(() -> {
                        closeProgressDialog();
                        if ("province".equals(type)) {
                            queryProvinces();
                        } else if ("city".equals(type)) {
                            queryCities();
                        } else if ("county".equals(type)) {
                            queryCounties();
                        }
                    });
                }
            }
        });
    }
    */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("第一次加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
