package com.bolyartech.forge.admin.units.user.user_manage;

import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.SimpleStateManagerImpl;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class Res_UserManageImpl extends SessionResidentComponent<Res_UserManage.State> implements Res_UserManage {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile long mDisableXId;

    private boolean mDisableResult;


    @Inject
    public Res_UserManageImpl(ForgeExchangeHelper forgeExchangeHelper,
                              Session session,
                              NetworkInfoProvider networkInfoProvider,
                              Bus bus) {

        super(new SimpleStateManagerImpl<>(bus, State.IDLE), forgeExchangeHelper, session, networkInfoProvider);
    }


    @Override
    public void disableUser(User user) {
        if (getState() == State.IDLE) {
            switchToState(State.DISABLING);
            disableEnable(user.getId(), true);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    private void disableEnable(long id, boolean disable) {
        ForgePostHttpExchangeBuilder b = createForgePostHttpExchangeBuilder("user_disable");
        b.addPostParameter("user", Long.toString(id));
        b.addPostParameter("disable", disable ? "1" : "0");

        ForgeExchangeManager em = getForgeExchangeManager();
        mDisableXId = em.generateTaskId();
        em.executeExchange(b.build(), mDisableXId);
    }


    @Override
    public void enableUser(User user) {
        if (getState() == State.IDLE) {
            switchToState(State.DISABLING);
            disableEnable(user.getId(), false);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public boolean getDisableResult() {
        return mDisableResult;
    }


    @Override
    public void onSessionExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mDisableXId) {
            handleDisableResult(isSuccess, result);
        }
    }


    private void handleDisableResult(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.Oks.OK.getCode()) {
                    try {
                        JSONObject jobj = new JSONObject(result.getPayload());
                        mDisableResult = jobj.getBoolean("disabled");
                        switchToState(State.DISABLE_OK);
                    } catch (JSONException e) {
                        switchToState(State.DISABLE_FAIL);
                    }
                } else {
                    switchToState(State.DISABLE_FAIL);
                }
            } else {
                switchToState(State.DISABLE_FAIL);
            }
        } else {
            switchToState(State.DISABLE_FAIL);
        }
    }


    @Override
    public void stateHandled() {
        if (isInOneOfStates(State.DISABLE_OK, State.DISABLE_FAIL)) {
            resetState();
        }
    }
}
