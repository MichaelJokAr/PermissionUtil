package com.github.jokar.permission_util;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.github.jokar.permission.PermissionUtil;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new PermissionUtil.Builder(this)
                .setPermissions(Manifest.permission.CAMERA)
                .setGrant(() -> {
                    Toast.makeText(getApplicationContext(), "Grant_Camera",
                            Toast.LENGTH_SHORT).show();
                    return null;
                })
                .setDenied(() -> {
                    Toast.makeText(getApplicationContext(), "Denied_Camera",
                            Toast.LENGTH_SHORT).show();

                    return null;
                })
                .request();

    }
}
