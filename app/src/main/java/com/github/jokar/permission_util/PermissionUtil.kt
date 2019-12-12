package com.github.jokar.permission_util

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.os.Build

/**
 * 权限请求
 * @Author: JokAr
 * @Date: 2019-12-11 16:09
 */

class PermissionUtil(
    var activity: Activity? = null,
    var fragment: Fragment? = null,
    var permission: String? = null,
    var permissions: Array<String>? = null,
    var grant: (() -> Unit)? = null,
    var denied: (() -> Unit)? = null,
    var neverAskAgain: (() -> Unit)? = null
) {
    var requestFragment: Fragment? = null
    var fragmentManager: FragmentManager? = null

    class Builder(
        private var a: Activity?,
        private val f: Fragment?
    ) {
        constructor(a: Activity) : this(a, null)
        constructor(f: Fragment) : this(null, f)

        private var p: String? = null
        private var ps: Array<String>? = null
        private var g: (() -> Unit)? = null
        private var d: (() -> Unit)? = null
        private var n: (() -> Unit)? = null

        fun setPermission(permission: String): Builder {
            this.p = permission
            return this
        }

        fun setPermissions(permissions: Array<String>): Builder {
            this.ps = permissions
            return this
        }

        fun setGrant(grant: () -> Unit): Builder {
            this.g = grant
            return this
        }

        fun setDenied(denied: (() -> Unit)): Builder {
            this.d = denied
            return this
        }

        fun setNeverAskAgain(neverAskAgain: (() -> Unit)): Builder {
            this.n = neverAskAgain
            return this
        }

        fun build() {
            PermissionUtil().apply {
                permission = p
                permissions = ps
                activity = a
                fragment = f
                grant = g
                denied = d
                neverAskAgain = n

                build()
            }
        }
    }

    private fun build() {
        if (activity == null && fragment == null) {
            throw RuntimeException("activity or fragment can't be null")
            return
        }

        if (permission.isNullOrEmpty() && permissions.isNullOrEmpty()) {
            throw RuntimeException("permission or permissions can't be null")
            return
        }
        //6.0以下直接返回允许
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            grant?.invoke()
            return
        }
        //
        activity?.let {
            fragmentManager = activity!!.fragmentManager
        }
        fragment?.let {
            fragmentManager = fragment!!.childFragmentManager
        }

        requestFragment = RequestFragment.instance(
            permission,
            permissions,
            {
                removeFragment(fragmentManager)
                grant?.invoke()
            },
            {
                removeFragment(fragmentManager)
                denied?.invoke()
            },
            {
                removeFragment(fragmentManager)
                neverAskAgain?.invoke()
            }
        )
        fragmentManager
            ?.beginTransaction()
            ?.add(requestFragment, "requestPermission")
            ?.commitAllowingStateLoss()
    }

    private fun removeFragment(fragmentManager: FragmentManager?) {
        fragmentManager
            ?.beginTransaction()
            ?.remove(requestFragment)
    }

}