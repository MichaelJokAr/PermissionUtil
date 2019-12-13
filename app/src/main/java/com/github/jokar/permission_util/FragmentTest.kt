package com.github.jokar.permission_util

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.github.jokar.permission.PermissionUtil

/**
 * @Author: JokAr
 * @Date: 2019-12-12 10:43
 * @Email: guidongqi@hupu.com
 */
class FragmentTest : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        PermissionUtil.Builder(this)
            .setPermissions(Manifest.permission.CAMERA)
            .setDenied {
                Toast.makeText(context, "Denied_RECORD_AUDIO", Toast.LENGTH_SHORT).show()
            }
            .setGrant {
                Toast.makeText(context, "grant_RECORD_AUDIO", Toast.LENGTH_SHORT).show()
            }
            .setNeverAskAgain {
                Toast.makeText(context, "NeverAskAgain_RECORD_AUDIO", Toast.LENGTH_SHORT)
                    .show()
            }
            .request()
    }


}