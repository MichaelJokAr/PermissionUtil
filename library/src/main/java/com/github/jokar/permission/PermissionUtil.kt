package com.github.jokar.permission

import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager

/**
 * 权限请求
 * @Author: JokAr
 * @Date: 2019-12-11 16:09
 */

class PermissionUtil(
    var activity: FragmentActivity? = null,
    var fragment: Fragment? = null
) {
    constructor(a: FragmentActivity) : this(a, null)
    constructor(f: Fragment) : this(null, f)


    private var permissions: Array<out String>? = null
    private var grant: (() -> Unit)? = null
    private var denied: (() -> Unit)? = null
    private var neverAskAgain: (() -> Unit)? = null

    private var requestFragment: Fragment? = null
    private var fragmentManager: FragmentManager? = null

    class Builder(
        private var a: FragmentActivity?,
        private val f: Fragment?
    ) {
        constructor(activity: FragmentActivity) : this(activity, null)
        constructor(fragment: Fragment) : this(null, fragment)

        private var ps: Array<out String>? = null
        private var g: (() -> Unit)? = null
        private var d: (() -> Unit)? = null
        private var n: (() -> Unit)? = null

        fun setPermissions(vararg permissions: String): Builder {
            this.ps = permissions
            return this
        }

        fun setGrant(grant: () -> Unit): Builder {
            this.g = grant
            return this
        }

        fun setDenied(denied: () -> Unit): Builder {
            this.d = denied
            return this
        }

        fun setNeverAskAgain(neverAskAgain: () -> Unit): Builder {
            this.n = neverAskAgain
            return this
        }


        fun request() {
            PermissionUtil(a, f).apply {
                permissions = ps
                grant = g
                denied = d
                neverAskAgain = n
            }.build()
        }
    }

    private fun build() {
        if (activity == null && fragment == null) {
            throw RuntimeException("activity or fragment can't be null")
            return
        }

        if (permissions.isNullOrEmpty()) {
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
            fragmentManager = activity!!.supportFragmentManager
        }
        fragment?.let {
            fragment!!.activity?.let {
                fragmentManager = fragment!!.childFragmentManager
            }
        }

        requestFragment = PermissionFragment.instance(
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
        requestFragment?.let {
            fragmentManager
                ?.beginTransaction()
                ?.add(requestFragment!!, "requestPermission")
                ?.commitAllowingStateLoss()
        }

    }

    /**
     * 移除fragment
     */
    private fun removeFragment(fragmentManager: FragmentManager?) {
        requestFragment?.let {
            fragmentManager
                ?.beginTransaction()
                ?.remove(requestFragment!!)
                ?.commitAllowingStateLoss()
        }
    }

}