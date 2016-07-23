package com.bolyartech.forge.admin.units.admin_user.admin_user_manage;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.ResidentComponentState;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import static com.bolyartech.forge.android.app_unit.ResidentComponentState.Type.*;


public interface Res_AdminUserManage extends StatefulResidentComponent<Res_AdminUserManage.State> {
    void disableUser(AdminUser user);
    void enableUser(AdminUser user);
    void delete(AdminUser user);

    void stateHandled();

    boolean getDisableResult();

    enum State implements ResidentComponentState {
        IDLE(START),
        DISABLING(TRANSIENT),
        DISABLE_OK(END),
        DISABLE_FAIL(END);

        private final Type mType;


        State(Type type) {
            mType = type;
        }


        public Type getType() {
            return mType;
        }
    }
}
