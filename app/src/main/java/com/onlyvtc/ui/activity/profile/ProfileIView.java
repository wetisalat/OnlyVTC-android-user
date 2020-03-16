package com.onlyvtc.ui.activity.profile;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.User;

public interface ProfileIView extends MvpView {

    void onSuccess(User user);

    void onUpdateSuccess(User user);

    void onError(Throwable e);

    void onSuccessPhoneNumber(Object object);

    void onVerifyPhoneNumberError(Throwable e);
}
