package com.mortex.accenture.task.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface DataBaseService {

    @Insert
    void insertData(TempModel tempModel);

    @Query("SELECT * FROM TempList")
    Observable<List<TempModel>> getData();

//    @Query(("SELECT * FROM TEMPLIST WHERE id = :id_"))
//    Observable<TempModel> getTemModel(Integer id_);

    @Delete
    void deleteItem(TempModel tempModel);
}
