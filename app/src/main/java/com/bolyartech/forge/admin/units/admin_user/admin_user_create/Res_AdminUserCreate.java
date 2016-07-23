package com.bolyartech.forge.admin.units.admin_user.admin_user_create;

import com.bolyartech.forge.android.app_unit.ResidentComponentState;
import com.bolyartech.forge.android.app_unit.StatefulResidentComponent;

import static com.bolyartech.forge.android.app_unit.ResidentComponentState.Type.*;


public interface Res_AdminUserCreate extends StatefulResidentComponent<Res_AdminUserCreate.State> {
    void stateHandled();
    void save(String username,
              String name,
              String password,
              boolean superAdmin);

    int getLastError();

    enum State implements ResidentComponentState {
        IDLE(START),
        SAVING(TRANSIENT),
        SAVE_OK(END),
        SAVE_FAIL(END);

        private final ResidentComponentState.Type mType;


        State(ResidentComponentState.Type type) {
            mType = type;
        }


        public ResidentComponentState.Type getType() {
            return mType;
        }
    }


}
