package com.onlyvtc.ui.activity.sms;

import com.onlyvtc.base.BasePresenter;
import com.onlyvtc.data.network.APIClient;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class GetSMSPresenter<V extends GetSMSIView> extends BasePresenter<V> implements GetSMSIPresenter<V> {

    @Override
    public void requestSMS(Map<String, String> params) {
        getCompositeDisposable().add(
                APIClient
                        .getAPIClient()
                        .requestSMS(params)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

}
