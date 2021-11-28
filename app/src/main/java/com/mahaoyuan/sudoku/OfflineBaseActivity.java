package com.mahaoyuan.sudoku;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.util.Calendar;

public class OfflineBaseActivity extends AppCompatActivity {
    private TimeChangeReceiver timeChangeReceiver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        unregisterReceiver(timeChangeReceiver);
    }

    class TimeChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            int hour = date.get(Calendar.HOUR_OF_DAY);
            if (hour < Config.PLAY_TIME_START || hour >= Config.PLAY_TIME_END){
                ResultDialog resultDialog = new ResultDialog(context,"游玩时间结束");
                resultDialog.setCancelable(false);
                resultDialog.show();
                Button resultBtn = resultDialog.findViewById(R.id.result_btn);
                resultBtn.setOnClickListener(v->{
                    resultDialog.cancel();
                    ActivityCollector.finishAll();
                });
            }
        }
    }
}
