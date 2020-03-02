package com.mortex.accenture.task.ui.weather_history;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mortex.accenture.task.data.local.AppDataBase;
import com.mortex.accenture.task.data.local.TempModel;
import com.mortex.accenture.task.data.model.GetTempResponse;
import com.mortex.accenture.task.data.network.ApiService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    @Inject
    ApiService apiService;

    @Inject
    AppDataBase appDataBase;

    String appId = "67e2f56864846490af3f44124bd0de02";

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSuccessful = new MutableLiveData<>();
    public MutableLiveData<Integer> errorCode = new MutableLiveData<>();
    public MutableLiveData<GetTempResponse> mGetTempResponse = new MutableLiveData<>();
    public MutableLiveData<List<TempModel>> mTempModel = new MutableLiveData<>();

    public Disposable disposable;
    public Disposable daoDisposable;

    public void getResult(String lat, String lon) {
        isLoading.setValue(true);

        Map<String, String> latValue = new HashMap<>();
        latValue.put("lat", lat);
        Map<String, String> lonValue = new HashMap<>();
        lonValue.put("lon", lon);
        Map<String, String> key = new HashMap<>();
        key.put("APPID", appId);

        disposable =
                apiService.getTempDetail(latValue, lonValue, key)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getTempResponse -> {
                            isLoading.setValue(false);
                            mGetTempResponse.setValue(getTempResponse);
                        }, throwable -> {
                            isLoading.setValue(false);
                            if (throwable instanceof IOException) {
                                IOException exception = (IOException) throwable;
                                if (exception.getMessage().length() < 5) {
                                    errorCode.setValue(Integer.valueOf(exception.getMessage()));
                                }
                            }
                        });
    }


    public void getFromDataBase() {
        isLoading.setValue(true);
        daoDisposable =
                appDataBase.dataBaseService().getData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tempModels -> {
                            isLoading.setValue(false);
                            mTempModel.setValue(tempModels);
                        }, throwable -> {
                            isLoading.setValue(false);
                            if (throwable instanceof IOException) {
                                IOException exception = (IOException) throwable;
                                if (exception.getMessage().length() < 5) {
                                    errorCode.setValue(Integer.valueOf(exception.getMessage()));
                                }
                            }
                        });
    }


}
