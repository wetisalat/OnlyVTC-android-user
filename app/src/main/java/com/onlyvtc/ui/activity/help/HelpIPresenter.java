package com.onlyvtc.ui.activity.help;


import com.onlyvtc.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface HelpIPresenter<V extends HelpIView> extends MvpPresenter<V> {
    void help();
}
