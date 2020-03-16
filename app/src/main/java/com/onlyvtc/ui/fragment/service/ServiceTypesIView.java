package com.onlyvtc.ui.fragment.service;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.Datum;
import com.onlyvtc.data.network.model.Service;

import java.util.List;

public interface ServiceTypesIView extends MvpView {

    void onSuccess(List<Service> serviceList);

    void onError(Throwable e);

    void onSuccess(Object object);

    void onSuccessPriceLogic(Datum datum);

    void onSuccessPoiDistance(List<Service> serviceList);

    void onSuccessService(List<Service> serviceList);
}
