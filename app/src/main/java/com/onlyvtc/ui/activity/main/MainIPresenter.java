package com.onlyvtc.ui.activity.main;

import com.onlyvtc.base.MvpPresenter;

import java.util.HashMap;

public interface MainIPresenter<V extends MainIView> extends MvpPresenter<V> {

    void getUserInfo();

    void logout(String id);

    void checkStatus();

    void getSavedAddress();

    void getProviders(HashMap<String, Object> params);

    void getNavigationSettings();

    void updateDestination(HashMap<String, Object> obj);

    void getAdsInfo();

    void checkDrivingZone(HashMap<String, Object> obj);

}
