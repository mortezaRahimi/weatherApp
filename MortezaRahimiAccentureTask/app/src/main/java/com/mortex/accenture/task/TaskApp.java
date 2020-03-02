package com.mortex.accenture.task;

import android.app.Application;
import android.content.Context;

import com.mortex.accenture.task.di.components.AppComponents;
import com.mortex.accenture.task.di.components.DaggerAppComponents;
import com.mortex.accenture.task.di.modules.AppModule;

public class TaskApp extends Application {

    private AppComponents mAppComponent;

    public AppComponents getAppComponent() {
        return mAppComponent;
    }

    private static Context context;


    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponents.builder().appModule(new AppModule(this)).build();


        context = this;


    }


}
