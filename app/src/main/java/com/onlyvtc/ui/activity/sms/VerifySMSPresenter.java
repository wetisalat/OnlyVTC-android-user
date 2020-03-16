package com.onlyvtc.ui.activity.sms;

import com.onlyvtc.base.BasePresenter;
import com.onlyvtc.data.network.APIClient;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VerifySMSPresenter<V extends VerifySMSIView> extends BasePresenter<V> implements VerifySMSIPresenter<V> {

    @Override
    public void verifySMS(Map<String, String> params) {
        getCompositeDisposable().add(
                APIClient
                        .getAPIClient()
                        .verifySMS(params)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

}
