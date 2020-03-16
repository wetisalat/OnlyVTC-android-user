package com.onlyvtc.ui.activity.past_trip_detail;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.Datum;

import java.util.List;

public interface PastTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> pastTripDetails);

    void onError(Throwable e);
}
