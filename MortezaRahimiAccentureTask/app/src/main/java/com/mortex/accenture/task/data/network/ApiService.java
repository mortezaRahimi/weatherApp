package com.mortex.accenture.task.data.network;

import com.mortex.accenture.task.data.model.GetTempResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiService {

    @GET(ApiConstants.GET_LOCAL_RESULT)
    Observable<GetTempResponse> getTempDetail(@QueryMap Map<String, String> lat, @QueryMap Map<String, String> lon, @QueryMap Map<String, String> appId);

}
