package com.onlyvtc.ui.activity.coupon;

import com.onlyvtc.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
