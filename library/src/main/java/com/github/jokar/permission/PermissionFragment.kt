package com.github.jokar.permission

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.jokar.permission.Utils.Companion.hasPermissions
import com.github.jokar.permission.Utils.Companion.verifyPermissions


/**
 * @Author: JokAr
 * @Date: 2019-12-11 16:31
 */
class PermissionFragment : Fragment() {
    private var grant: (() -> Unit)? = null
    private var denied: (() -> Unit)? = null
    private var neverAskAgain: (() -> Unit)? = null

    private val REQUEST_CODE = 100

    private var permissions: Array<String> = arrayOf()

    companion object {
        private val KEY_PERMISSIONS = "permissions"

        fun instance(
            permissions: Array<out String>? = null,
            grant: () -> Unit,
            denied: () -> Unit,
            neverAskAgain: () -> Unit
        ): PermissionFragment {
            val fragment = PermissionFragment()
            val bundle = Bundle()
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
        when (requestCode) {
            REQUEST_CODE -> {
                if (verifyPermissions(*grantResults)) {
                    //同意了权限
                    grant?.invoke()
                } else {
                    if (!Utils.shouldShowRequestPermissionRationale(activity!!, *permissions)) {
                        //不在提示
                        neverAskAgain?.invoke()
                    } else {
                        //拒绝了权限
                        denied?.invoke()
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun request() {
        if (activity == null || context == null) {
            denied?.invoke()
            return
        }
        arguments?.run {
            getStringArray(KEY_PERMISSIONS)?.let {
                permissions = it
            }
        }

        if (permissions.isNullOrEmpty()) {
            return
        }
        //判断权限
        if (!hasPermissions(context!!, *permissions)) {
            //没有权限-请求权限
            if (Utils.shouldShowRequestPermissionRationale(activity!!, *permissions)) {
                denied?.invoke()
            } else {
                //请求权限
                requestPermissions(permissions, REQUEST_CODE)
            }
        } else {
            //有权限，直接打开
            grant?.invoke()
        }
    }


    override fun onStart() {
        super.onStart()
        retainInstance = true
        request()
    }
}