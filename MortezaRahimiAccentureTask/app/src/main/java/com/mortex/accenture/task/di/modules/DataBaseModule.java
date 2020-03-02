package com.mortex.accenture.task.di.modules;

import androidx.room.Room;

import com.mortex.accenture.task.TaskApp;
import com.mortex.accenture.task.data.local.AppDataBase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataBaseModule {

    @Singleton
    @Provides
    AppDataBase appDatabase(){
        return Room.databaseBuilder(TaskApp.getContext(), AppDataBase.class, TaskApp.getContext().getPackageName() + ".database").allowMainThreadQueries().build();
    }
}
