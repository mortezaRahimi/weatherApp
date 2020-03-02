package com.mortex.accenture.task.di.modules;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
final class ApiHeaders implements Interceptor {
    private final Application app;


    @Inject
    public ApiHeaders(Application app) {
        this.app = app;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
//            we initialize the request and change its header depending on whether
//            the device is connected to Internet or not.
        Request.Builder request = chain.request().newBuilder().addHeader("Accept", "application/json");
        if (isNetworkAvailable()) {
            /*
             *  If there is Internet, get the cache that was stored 5 seconds ago.
             *  If the cache is older than 5 seconds, then discard it,
             *  and indicate an error in fetching the response.
             *  The 'max-age' attribute is responsible for this behavior.
             */
            int maxAge = 60; // read from cache for 1 minute
            request.addHeader("Cache-Control", "public, max-age=" + maxAge);
        } else {
            /*
             *  If there is no Internet, get the cache that was stored 7 days ago.
             *  If the cache is older than 7 days, then discard it,
             *  and indicate an error in fetching the response.
             *  The 'max-stale' attribute is responsible for this behavior.
             *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
             */
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-week stale
            request.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + maxStale);
        }
        return chain.proceed(request.build());
    }

    /**
     * Is network available boolean.
     *
     * @return a boolean indicating if network is available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

