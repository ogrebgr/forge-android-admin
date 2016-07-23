package com.bolyartech.forge.admin.units.user.users;

import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import java.util.List;


public interface Res_Users extends StatefulResidentComponent<Res_Users.State> {
    void searchForUser(String pattern);

    List<User> getData();

    void stateHandled();

    enum State {
        IDLE,
        WAITING_DATA,
        DATA_OK,
        DATA_FAIL
    }
}
