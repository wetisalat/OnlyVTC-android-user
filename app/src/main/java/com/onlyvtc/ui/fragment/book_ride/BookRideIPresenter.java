package com.onlyvtc.ui.fragment.book_ride;

import com.onlyvtc.base.MvpPresenter;

import java.util.HashMap;


public interface BookRideIPresenter<V extends BookRideIView> extends MvpPresenter<V> {
    void rideNow(HashMap<String, Object> obj);

    void getCouponList();
}
