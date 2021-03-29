package com.example.weather.startpage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weather.R;
import com.example.weather.concretepage.ConcretePageActivity;
import com.example.weather.data.db.TianqiLeader;
import com.example.weather.data.db.TianqiProvince;

public class StartPageFragment extends Fragment implements StartPageContract.View {

    private StartPageContract.Presenter mStartPagePresenter;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_page_fragment, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //mStartPagePresenter will not be null after Activity's onCreate finished.
        //listView.setAdapter(mStartPagePresenter.getAdapter());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //set listener here using mvp.
        super.onActivityCreated(savedInstanceState);
        listView.setAdapter(mStartPagePresenter.getAdapter());
        listView.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {
                    if (mStartPagePresenter.getCurrentLevel() ==
                            StartPagePresenter.LEVEL_PROVINCE) {
                        TianqiProvince selectedProvince = mStartPagePresenter.getProvinceList().get(position);
                        mStartPagePresenter.setSelectedProvince(selectedProvince);
                        mStartPagePresenter.queryLeaders();
                    } else if (mStartPagePresenter.getCurrentLevel() ==
                            StartPagePresenter.LEVEL_LEADER) {
                        TianqiLeader selectedLeader = mStartPagePresenter.getLeaderList().get(position);
                        mStartPagePresenter.setSelectedLeader(selectedLeader);
                        mStartPagePresenter.queryCities();
                    } else if (mStartPagePresenter.getCurrentLevel() == StartPagePresenter.LEVEL_CITY) {
                        String cityId = mStartPagePresenter.getCityList().get(position).getCityId();
                        if (getActivity() instanceof StartPageActivity) {
                            Intent intent = new Intent(getActivity(), ConcretePageActivity.class);
                            intent.putExtra("city_id", cityId);
                            startActivity(intent);
                            getActivity().finish();
                        } else if (getActivity() instanceof ConcretePageActivity) {
                            ConcretePageActivity activity = (ConcretePageActivity) getActivity();
                            activity.drawerLayout.closeDrawers();
                            activity.swipeRefresh.setRefreshing(true);
                            activity.requestWeather(cityId);
                        }
                    }
                });
        backButton.setOnClickListener(v -> {
            if (mStartPagePresenter.getCurrentLevel() == StartPagePresenter.LEVEL_CITY) {
                mStartPagePresenter.queryLeaders();
            } else if (mStartPagePresenter.getCurrentLevel() == StartPagePresenter.LEVEL_LEADER) {
                mStartPagePresenter.queryProvinces();
            }
        });
        mStartPagePresenter.queryProvinces();
    }

    @Override
    public void setPresenter(StartPageContract.Presenter presenter) {
        mStartPagePresenter = presenter;
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("第一次加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public Activity getActivityForView() {
        return getActivity();
    }

    public TextView getTitleText() {
        return titleText;
    }

    public Button getBackButton() {
        return backButton;
    }

    public ListView getListView() {
        return listView;
    }


}
