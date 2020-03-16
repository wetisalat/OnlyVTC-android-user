package com.onlyvtc.ui.activity.social;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.Token;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView {
    void onSuccess(Token token);

    void onError(Throwable e);
}
