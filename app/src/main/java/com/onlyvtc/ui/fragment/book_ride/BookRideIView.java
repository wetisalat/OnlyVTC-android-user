package com.onlyvtc.ui.fragment.book_ride;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.PromoResponse;


public interface BookRideIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccessCoupon(PromoResponse promoResponse);
}
