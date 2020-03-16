package com.onlyvtc.ui.activity.upcoming_trip_detail;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.Datum;

import java.util.List;

public interface UpcomingTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> upcomingTripDetails);

    void onSuccess(Object o);

    void onError(Throwable e);
}
