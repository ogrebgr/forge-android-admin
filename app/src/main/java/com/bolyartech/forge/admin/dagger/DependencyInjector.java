package com.bolyartech.forge.admin.dagger;

public class DependencyInjector {
    private static AppDaggerComponent mDependencyInjector;

    private DependencyInjector() {
        throw new AssertionError("No instances allowed");
    }


    public static void init(AppDaggerComponent di) {
        if (mDependencyInjector == null) {
            mDependencyInjector = di;
        } else {
            throw new IllegalStateException("Already initialized");
        }
    }


    public static AppDaggerComponent getInstance() {
        if (mDependencyInjector == null) {
            throw new IllegalStateException("Not initialized. You must call init().");
        }
        return mDependencyInjector;
    }
}
