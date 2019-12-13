# 这可能是最好用实现最简单的权限请求库了

## 使用
[ ![Download](https://api.bintray.com/packages/a10188755550/maven/permission/images/download.svg) ](https://bintray.com/a10188755550/maven/permission/_latestVersion)
```gradle
implementation 'com.github.jokar:permission:${latest-version}'
```

## **为什么最好用？**
该工具库使用```Builder```设计模式，可以说是非常简单上手的工具了，使用方法如下：
- ```kotlin```
```
 PermissionUtil.Builder(this)
            .setPermissions(Manifest.permission.RECORD_AUDIO)
            .setDenied {
                Toast.makeText(applicationContext, "Denied_RECORD_AUDIO", Toast.LENGTH_SHORT).show()
            }
            .setGrant {
                Toast.makeText(applicationContext, "grant_RECORD_AUDIO", Toast.LENGTH_SHORT).show()
            }
            .setNeverAskAgain {
                Toast.makeText(applicationContext, "NeverAskAgain_RECORD_AUDIO", Toast.LENGTH_SHORT)
                    .show()
            }
            .request()
```
- ```Java```
```
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
```

## **为什么实现方式最简单？**
该库利用了```kotlin```来进行传参做到了代码的精简化，权限请求实现的原理是利用```Fragment```实现,在请求权限时动态添加一个```Fragment```在该```Fragment```里做请求权限，完成后移除```Fragment```。

- ```PermissionUtil```的核心方法
```
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
            fragmentManager = it.supportFragmentManager
        }
        fragment?.let {
            fragmentManager = it.childFragmentManager
        }

        requestFragment = PermissionFragment.instance(
            permissions,
            {
                removeFragment()
                grant?.invoke()
            },
            {
                removeFragment()
                denied?.invoke()
            },
            {
                removeFragment()
                neverAskAgain?.invoke()
            }
        )
        requestFragment?.let { fragment ->
            fragmentManager?.run {
                beginTransaction()
                    .add(fragment, "requestPermission")
                    .commitAllowingStateLoss()
            }
        }
    }
```

- PermissionFragment的核心方法
```

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
```

大致实现就这四个方法，至于权限判断则封装在工具类里，可以看到实现过程非常简单明了