package com.github.jokar.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v4.util.SimpleArrayMap
import android.text.TextUtils

/**
 * 权限辅助类
 * @Author: JokAr
 * @Date: 2019-12-11 16:31
 */
class PermissionHelper {
    companion object {
        private var MIN_SDK_PERMISSIONS: SimpleArrayMap<String, Int> = SimpleArrayMap(8)

        init {
            MIN_SDK_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14)
            MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20)
            MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16)
            MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16)
            MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9)
            MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16)
            MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23)
            MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23)
        }

        private fun permissionExists(permission: String): Boolean {
            if (TextUtils.isEmpty(permission)) {
                return false
            }
            val minVersion = MIN_SDK_PERMISSIONS.get(permission)
            return minVersion == null || Build.VERSION.SDK_INT >= minVersion
        }


        /**
         * 检测权限
         */
        private fun hasSelfPermission(
            context: Context,
            permission: String
        ): Boolean {
            return try {
                PermissionChecker.checkSelfPermission(
                    context,
                    permission
                ) === PackageManager.PERMISSION_GRANTED
            } catch (t: RuntimeException) {
                false
            }
        }


        /**
         * 检查是否有权限组
         */
        fun hasPermissions(
            context: Context,
            vararg permissions: String
        ): Boolean {
            if (context == null || permissions.isNullOrEmpty()) {
                return false
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true
            }
            permissions.forEach {
                if (permissionExists(it) && !hasSelfPermission(
                        context, it
                    )
                ) {
                    return false
                }
            }
            return true
        }

        /**
         *
         */
        fun shouldShowRequestPermissionRationale(
            activity: Activity,
            vararg permissions: String
        ): Boolean {
            if (activity == null || permissions.isNullOrEmpty()) {
                return false
            }
            return permissions.all {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            }
        }

        /**
         * 验证权限
         */
        fun verifyPermissions(vararg grantResults: Int): Boolean {
            if (grantResults.isEmpty()) {
                return false
            }
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }
}