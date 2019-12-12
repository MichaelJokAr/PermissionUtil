package com.github.jokar.permission_util;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new PermissionUtil.Builder(this)
                .setPermission(Manifest.permission.CAMERA)
                .setGrant(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        return null;
                    }
                })
                .build();

    }
}
