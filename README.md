## PermissionUtil
这可能是最好用、简单的方式实现权限请求了

### 为什么最好用？

- kotlin用法
```
PermissionUtil.Builder(this)
            .setPermissions(Manifest.permission.CAMERA)
            .setDenied {
                Toast.makeText(applicationContext, "Denied_CAMERA", Toast.LENGTH_SHORT).show()
            }
            .setGrant {
                Toast.makeText(applicationContext, "grant_CAMERA", Toast.LENGTH_SHORT).show()
            }
            .setNeverAskAgain {
                Toast.makeText(applicationContext, "NeverAskAgain_CAMERA", Toast.LENGTH_SHORT)
                    .show()
            }
            .request()
```
- java用法
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

可以看到使用Builder设计模式来构建使用方法,上手不需要1min.特别方便

### 为什么实现最简单？
使用fragment来实现的权限请求，总共不超过150行代码非常简单明了