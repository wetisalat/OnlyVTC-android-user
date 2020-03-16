package com.onlyvtc.ui.fragment.service;

import com.onlyvtc.base.MvpPresenter;
import com.onlyvtc.data.network.model.Datum;

import java.util.HashMap;

public interface ServiceTypesIPresenter<V extends ServiceTypesIView> extends MvpPresenter<V> {

    void services();

    void rideNow(HashMap<String, Object> obj);

    void checkPoiPriceLogic(HashMap<String, Object> obj);

    void checkPoiDistanceInfo(HashMap<String, Object> obj);

    void getServiceType(HashMap<String, Object> obj);

}
