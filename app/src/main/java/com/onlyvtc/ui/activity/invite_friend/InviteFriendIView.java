package com.onlyvtc.ui.activity.invite_friend;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.User;

public interface InviteFriendIView extends MvpView {

    void onSuccess(User user);

    void onError(Throwable e);

}
