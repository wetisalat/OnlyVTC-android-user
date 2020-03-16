package com.onlyvtc.ui.activity.help;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.Help;

public interface HelpIView extends MvpView {

    void onSuccess(Help help);

    void onError(Throwable e);
}
