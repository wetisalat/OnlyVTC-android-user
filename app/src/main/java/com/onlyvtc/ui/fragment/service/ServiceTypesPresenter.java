package com.onlyvtc.ui.fragment.service;

import com.onlyvtc.base.BasePresenter;
import com.onlyvtc.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ServiceTypesPresenter<V extends ServiceTypesIView> extends BasePresenter<V> implements ServiceTypesIPresenter<V> {

    @Override
    public void services() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .services()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void rideNow(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
//                .sendRequest(obj) //called when old code
                .sendRequests(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void checkPoiPriceLogic(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .checkPoiPriceLogic(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessPriceLogic, getMvpView()::onError));
    }

    @Override
    public void checkPoiDistanceInfo(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .getServicePOIDisatnceInfo(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessPoiDistance, getMvpView()::onError));
    }

    @Override
    public void getServiceType(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .getServiceType(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessService, getMvpView()::onError));
    }

}
