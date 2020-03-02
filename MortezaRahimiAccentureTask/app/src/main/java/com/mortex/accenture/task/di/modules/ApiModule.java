package com.mortex.accenture.task.di.modules;

import android.app.Application;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.mortex.accenture.task.data.network.ApiConstants;
import com.mortex.accenture.task.data.network.ApiService;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

@Module
public class ApiModule {
    //The following line of code specifies a cache of 5MB. Note that it needs to be a Long
    private static final long DISK_CACHE_SIZE = (5 * 1024 * 1024);

    static OkHttpClient okHttpClient(Application app, ApiHeaders apiHeaders) {
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(apiHeaders)
                .writeTimeout(10, SECONDS)
                .readTimeout(10, SECONDS)
                .connectTimeout(10, SECONDS)
                .cache(cache)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);

                    if (response.code() == 200 || response.code() == 201) {

                    } else
                        throw new IOException(String.valueOf(response.code()));

                    assert response.body() != null;
                    Log.i("response", "responseBody" + response.body().toString());

                    assert response.message() != null;
                    Log.i("response", "response message" + response.message());
                    return response;
                })
                .build();

        return client;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }


    @Provides
    @Singleton
    ApiService provideApiService(Retrofit builder) {
        return builder.create(ApiService.class);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app, ApiHeaders headers) {
        return okHttpClient(app, headers);
    }
}
