package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.ResidentComponentState;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import java.util.List;

import static com.bolyartech.forge.android.app_unit.ResidentComponentState.Type.*;


public interface Res_AdminUsersList extends StatefulResidentComponent<Res_AdminUsersList.State> {
    void loadAdminUsers();

    List<AdminUser> getData();

    void stateHandled();

    enum State implements ResidentComponentState {
        IDLE(START),
        WAITING_DATA(TRANSIENT),
        DATA_OK(END),
        DATA_FAIL(END);

        private final ResidentComponentState.Type mType;


        State(ResidentComponentState.Type type) {
            mType = type;
        }


        public ResidentComponentState.Type getType() {
            return mType;
        }
    }
}
