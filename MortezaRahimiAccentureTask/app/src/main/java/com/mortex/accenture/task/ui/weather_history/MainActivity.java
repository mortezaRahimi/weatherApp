package com.mortex.accenture.task.ui.weather_history;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mortex.accenture.task.R;
import com.mortex.accenture.task.TaskApp;
import com.mortex.accenture.task.data.local.TempModel;
import com.mortex.accenture.task.data.model.GetTempResponse;
import com.mortex.accenture.task.data.model.Temperature;
import com.mortex.accenture.task.ui.base.BaseActivity;
import com.mortex.accenture.task.ui.util.BroadcastService;
import com.mortex.accenture.task.ui.util.SwipeToDeleteCallback;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

import static io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private MainViewModel mainViewModel;
    private WeatherAdapter adapter;
    private String lat;
    private String lon;
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    List<GetTempResponse> list = new ArrayList<>();
    @BindView(R.id.weather_rv)
    RecyclerView rv;
    @BindView(R.id.swipe_view)
    SwipeRefreshLayout swipeView;
    @BindView(R.id.update_tv)
    TextView updateTv;
    @BindView(R.id.pull_down_text)
    TextView pulDownText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        ((TaskApp) getApplication()).getAppComponent().inject(mainViewModel);

        swipeView.setOnRefreshListener(this);

        startService(new Intent(this, BroadcastService.class));

        initWeatherRv();

        attachErrorObserver();

        mainViewModel.getFromDataBase();

        attachDataBaseResult();

        attachLoadingObserver();

        getResultObserver();

    }

    @Override
    protected int getViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.btn_save)
    public void getUserLocation() {
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        startLocation();
                    } else {
                        showMessageDialog(getString(R.string.permission_info));
                        getMessageDialog().findViewById(R.id.btn_ok).setOnClickListener(view -> {
                            dismissMessageDialog();
                            getUserLocation();
                        });
                    }
                });
    }

    private void startLocation() {
        showLoading(true);
        LocationGooglePlayServicesProvider provider = new LocationGooglePlayServicesProvider();


        if (lat != null && lon != null) {
            mainViewModel.getResult(lat, lon);
        } else {
            displayLocationSettingsRequest(this);
            SmartLocation.with(MainActivity.this).location(provider).oneFix().start(location -> {
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());

                Log.d("Latitude", lat);
                Log.d("Longitude", lon);

                mainViewModel.getResult(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                SmartLocation.with(this).location().stop();
            });
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Log.i("ee", "All location settings are satisfied.");
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i("c", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i("dcd", "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.i("dvv", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_CANCELED) {
                showMessageDialog(getString(R.string.permission_info));
                getMessageDialog().findViewById(R.id.btn_ok).setOnClickListener(view -> {
                            dismissMessageDialog();
                            getUserLocation();
                        }
                );
            } else if (resultCode == RESULT_OK)
                showLoading(true);
        }

    }

    private void getResultObserver() {
        mainViewModel.mGetTempResponse.observe(this, getTempResponse -> {
            mainViewModel.appDataBase.dataBaseService().insertData(new TempModel(null, getTempResponse.getMain().getTemp(), getTempResponse.getDt(), getTempResponse.getName()));
        });
    }

    private void attachDataBaseResult() {
        mainViewModel.mTempModel.observe(this, tempModels -> {
            if (tempModels.size() > 0) {
                list.clear();
                for (int i = 0; i < tempModels.size(); i++) {
                    Temperature temperature = new Temperature(tempModels.get(i).getTemp());
                    list.add(new GetTempResponse(temperature, tempModels.get(i).getId(), tempModels.get(i).getDate(), tempModels.get(i).getCity()));
                }
                adapter.replaceWith(list);
                pulDownText.setVisibility(View.GONE);
                rv.smoothScrollToPosition(list.size());
            } else {
                pulDownText.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, BroadcastService.class));
        super.onDestroy();
        if (mainViewModel.disposable != null)
            mainViewModel.disposable.dispose();
        if (mainViewModel.daoDisposable != null)
            mainViewModel.daoDisposable.dispose();
    }

    private void attachErrorObserver() {
        mainViewModel.errorCode.observe(this, errorCode -> {
            if (errorCode != null) {
                Log.d("Error:", String.valueOf(errorCode));
                switch (errorCode) {
                    case 401:
                    case 400:
                        showToast("error:" + errorCode);
                        break;
                }
            }
        });
    }

    private void initWeatherRv() {
        adapter = new WeatherAdapter(this);
        rv.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(RecyclerView.VERTICAL);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(rv);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
    }

    private void attachLoadingObserver() {
        mainViewModel.isLoading.observe(this, this::showLoading);
    }

    public void showLoading(boolean show) {
        if (show) {
            if (!swipeView.isRefreshing()) {
                swipeView.setRefreshing(true);
            }
        } else {
            if (swipeView.isRefreshing())
                swipeView.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        getUserLocation();
    }

    public void removeFromDb(GetTempResponse mRecentlyDeletedItem) {
        mainViewModel.appDataBase.dataBaseService().deleteItem(new TempModel(mRecentlyDeletedItem.getId(), mRecentlyDeletedItem.getMain().getTemp(), mRecentlyDeletedItem.getDt(), mRecentlyDeletedItem.getName()));
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            if (millisUntilFinished > 0) {
                String s = " will refresh in " + " 00:" + millisUntilFinished / 1000;
                updateTv.setText(s);
            } else {
                swipeView.setRefreshing(true);
                getUserLocation();
            }
        }
    }
}
