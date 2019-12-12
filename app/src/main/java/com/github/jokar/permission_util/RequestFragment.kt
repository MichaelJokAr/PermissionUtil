package com.github.jokar.permission_util

import android.annotation.TargetApi
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat


/**
 * @Author: JokAr
 * @Date: 2019-12-11 16:31
 * @Email: guidongqi@hupu.com
 */
class RequestFragment : Fragment() {
    private var grant: (() -> Unit)? = null
    private var denied: (() -> Unit)? = null
    private var neverAskAgain: (() -> Unit)? = null

    private val REQUEST_CODE = 100

    private var permissions: Array<String> = arrayOf()

    companion object {
        private val KEY_PERMISSION = "permission"
        private val KEY_PERMISSIONS = "permissions"

        fun instance(
            permission: String? = null,
            permissions: Array<String>? = null,
            grant: () -> Unit,
            denied: () -> Unit,
            neverAskAgain: () -> Unit
        ): RequestFragment {
            val fragment = RequestFragment()
            val bundle = Bundle()
            bundle.putString(KEY_PERMISSION, permission)
            bundle.putStringArray(KEY_PERMISSIONS, permissions)
            fragment.arguments = bundle
            //
            fragment.grant = grant
            fragment.denied = denied
            fragment.neverAskAgain = neverAskAgain
            return fragment
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意了权限
                grant?.invoke()
            } else {
                //拒绝了权限
                denied?.invoke()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun request() {
        if (activity == null) {
            return
        }
        arguments?.let {
            arguments.getString(KEY_PERMISSION)?.let {
                permissions = arrayOf(arguments.getString(KEY_PERMISSION) as String)
                permissions.plus(arguments.getString(KEY_PERMISSION) as String)
            }

            arguments.getStringArray(KEY_PERMISSIONS)?.let {
                permissions = arguments.getStringArray(KEY_PERMISSIONS) as Array<String>
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(permissions)) {
                //没有权限-请求权限
                if (shouldShowRequestPermissionRationale(permissions)) {
                    //选择了不再询问
                    neverAskAgain?.invoke()
                } else {
                    //请求权限
                    requestPermissions(permissions, REQUEST_CODE)
                }
            } else {
                //有权限，直接打开
                grant?.invoke()
            }
        } else {
            //6.0以下直接打开
            grant?.invoke()
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }

    override fun onStart() {
        super.onStart()
        retainInstance = true
        request()
    }
}