package com.onlyvtc.ui.activity.past_trip_detail;

import com.onlyvtc.base.MvpPresenter;

public interface PastTripDetailsIPresenter<V extends PastTripDetailsIView> extends MvpPresenter<V> {

    void getPastTripDetails(Integer requestId);
}
