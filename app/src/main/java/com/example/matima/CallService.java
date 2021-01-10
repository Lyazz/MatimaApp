package com.example.matima;


import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;

import androidx.core.app.NotificationCompat;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        new OngoingCall().setCall(call);
        CallActivity.start(this, call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
    }
}