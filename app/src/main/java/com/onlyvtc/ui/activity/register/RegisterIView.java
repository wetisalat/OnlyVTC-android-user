package com.onlyvtc.ui.activity.register;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.RegisterResponse;
import com.onlyvtc.data.network.model.SettingsResponse;

public interface RegisterIView extends MvpView {

    void onSuccess(SettingsResponse response);

    void onSuccess(RegisterResponse object);

    void onSuccess(Object object);

    void onSuccessPhoneNumber(Object object);

    void onVerifyPhoneNumberError(Throwable e);

    void onError(Throwable e);

    void onVerifyEmailError(Throwable e);

    void onSuccessTermsConditions(Object o);
}
