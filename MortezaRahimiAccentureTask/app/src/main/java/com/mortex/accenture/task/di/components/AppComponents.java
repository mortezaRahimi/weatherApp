package com.mortex.accenture.task.di.components;

import com.mortex.accenture.task.di.modules.ApiModule;
import com.mortex.accenture.task.di.modules.AppModule;
import com.mortex.accenture.task.di.modules.DataBaseModule;
import com.mortex.accenture.task.ui.weather_history.MainViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class,
        ApiModule.class,
        DataBaseModule.class})
public interface AppComponents {

    void inject(MainViewModel mainViewModel);

}
