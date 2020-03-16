package com.onlyvtc.ui.activity.setting;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
