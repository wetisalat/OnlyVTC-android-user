package com.onlyvtc.ui.activity.login;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.ForgotResponse;
import com.onlyvtc.data.network.model.Token;

public interface LoginIView extends MvpView {
    void onSuccess(Token token);

    void onSuccess(ForgotResponse object);

    void onError(Throwable e);
}
