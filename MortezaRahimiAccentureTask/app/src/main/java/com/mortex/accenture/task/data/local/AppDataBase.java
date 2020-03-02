package com.mortex.accenture.task.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {TempModel.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public abstract DataBaseService dataBaseService();

}
