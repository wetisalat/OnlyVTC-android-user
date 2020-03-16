package com.onlyvtc.ui.activity.card;

import com.onlyvtc.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
