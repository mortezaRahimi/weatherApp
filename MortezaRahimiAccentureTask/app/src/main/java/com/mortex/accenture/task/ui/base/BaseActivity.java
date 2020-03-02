package com.mortex.accenture.task.ui.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mortex.accenture.task.R;

import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

    private MaterialDialog messageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getViewId());

        initDialogs();
    }

    private void initDialogs() {

        messageDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.base_dialog, false)
                .cancelable(false).build();
        Objects.requireNonNull(messageDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Objects.requireNonNull(messageDialog.getCustomView()).setFitsSystemWindows(true);

        messageDialog.findViewById(R.id.btn_ok).setOnClickListener(view -> messageDialog.dismiss());
    }

    protected abstract int getViewId();

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showMessageDialog(String title) {

        ((TextView) messageDialog.findViewById(R.id.title)).setText(title);

        messageDialog.show();

    }

    public MaterialDialog getMessageDialog() {
        return messageDialog;
    }

    public void dismissMessageDialog() {
        messageDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}