package com.github.jokar.permission_util

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.jokar.permission.PermissionUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text.setOnClickListener {
            request()
        }
        audio.setOnClickListener {
            audioRequest()
        }
    }

    fun audioRequest() {
        PermissionUtil.Builder(this)
            .setPermission(Manifest.permission.RECORD_AUDIO)
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
    }

    fun request() {
        PermissionUtil.Builder(this)
            .setPermission(Manifest.permission.CAMERA)
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
    }
}
