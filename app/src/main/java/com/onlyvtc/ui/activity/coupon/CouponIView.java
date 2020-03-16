package com.onlyvtc.ui.activity.coupon;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);

    void onError(Throwable e);
}
