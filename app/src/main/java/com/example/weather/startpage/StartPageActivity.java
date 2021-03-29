package com.example.weather.startpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.R;
import com.example.weather.concretepage.ConcretePageActivity;
import com.example.weather.data.TasksRepository;

public class StartPageActivity extends AppCompatActivity {

    private StartPagePresenter mStartPagePresenter;
    private StartPageFragment mStartPageFragment;
    private long mExitTime = System.currentTimeMillis() - 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page_activity);
        mStartPageFragment = (StartPageFragment)getSupportFragmentManager()
                .findFragmentById(R.id.start_page_fragment);
        mStartPagePresenter = new StartPagePresenter(TasksRepository.getInstance(),
                mStartPageFragment);
        SharedPreferences prefs = mStartPagePresenter.getDefaultSharedPreferences(this);
        if (prefs.getString("weather", null) != null) {
            Intent intent = new Intent(this, ConcretePageActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        if (mStartPagePresenter.getCurrentLevel() != StartPagePresenter.LEVEL_PROVINCE) {
            mStartPagePresenter.onBackPressed();
        } else if (mStartPagePresenter.getCurrentLevel()
                == StartPagePresenter.LEVEL_PROVINCE) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按返回键将退出应用", Toast.LENGTH_SHORT)
                        .show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }
}
