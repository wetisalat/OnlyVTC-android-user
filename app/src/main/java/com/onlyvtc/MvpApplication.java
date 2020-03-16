package com.onlyvtc;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.onlyvtc.common.ConnectivityReceiver;
import com.onlyvtc.common.LocaleHelper;
import com.onlyvtc.data.network.model.Datum;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

//import com.facebook.stetho.Stetho;

public class MvpApplication extends Application {

    private static MvpApplication mInstance;

    public static boolean canGoToChatScreen;
    public static boolean isChatScreenOpen;

    public static boolean isCash = true;
    public static boolean isCard = true;
    public static boolean isPayumoney;
    public static boolean isPaypal;
    public static boolean isPaytm;
    public static boolean isPaypalAdaptive;
    public static boolean isBraintree;
    public static boolean openChatFromNotification = true;

    public static HashMap<String, Object> RIDE_REQUEST = new HashMap<>();
    public static Datum DATUM = null;
    public static boolean showOTP = true;

    public static synchronized MvpApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Crashlytics crashlytics = new Crashlytics.Builder().core(core).build();
        Fabric.with(this, crashlytics);

        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this);

        MultiDex.install(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "fr"));
        MultiDex.install(newBase);
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
