package com.onlyvtc.ui.activity.main;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.AddressResponse;
import com.onlyvtc.data.network.model.Advertisement;
import com.onlyvtc.data.network.model.DataResponse;
import com.onlyvtc.data.network.model.Datum;
import com.onlyvtc.data.network.model.Provider;
import com.onlyvtc.data.network.model.SettingsResponse;
import com.onlyvtc.data.network.model.User;

import java.util.List;

public interface MainIView extends MvpView {

    void onSuccess(User user);

    void onSuccess(DataResponse dataResponse);

    void onDestinationSuccess(Object object);

    void onSuccessLogout(Object object);

    void onSuccess(AddressResponse response);

    void onSuccess(List<Provider> objects);

    void onError(Throwable e);

    void onSuccess(SettingsResponse response);

    void onSettingError(Throwable e);

    void onAdsSuccess(List<Advertisement> array);

    void onSuccessDrivingZone(Datum datum);


}
