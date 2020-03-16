package com.onlyvtc.ui.activity.splash;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.CheckVersion;
import com.onlyvtc.data.network.model.Service;
import com.onlyvtc.data.network.model.User;

import java.util.List;

public interface SplashIView extends MvpView {

    void onSuccess(List<Service> serviceList);

    void onSuccess(User user);

    void onError(Throwable e);

    void onSuccess(CheckVersion checkVersion);
}
