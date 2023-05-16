package svg.viewer.svg.converter.reader.activities

import android.Manifest
import android.bluetooth.BluetoothGattCharacteristic
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import svg.viewer.svg.converter.reader.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                val intent = Intent(this@PermissionActivity, SplashActivity::class.java)
                startActivity(intent)
            } else {
                setContentView(binding.root)
            }
        } else {
            if (!checkPermission()) {
                setContentView(binding.root)
            } else {
                val intent = Intent(this@PermissionActivity, SplashActivity::class.java)
                startActivity(intent)


            }
        }

        binding.btnAccept.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, 2296)
                }
            } else {
                if (!checkPermission()) {
                    requestPermission()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BluetoothGattCharacteristic.PERMISSION_READ) {
            if (grantResults.isNotEmpty()) {
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted) {
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showMessageOKCancel { _: DialogInterface?, _: Int ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                requestPermissions(
                                    arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ),
                                    BluetoothGattCharacteristic.PERMISSION_READ
                                )
                            }
                        }
                    }
                }
            }
            val intent = Intent(this@PermissionActivity, SplashActivity::class.java)
            startActivity(intent)
        } else {
            setContentView(binding.root)
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                val intent1 = Intent(this, SplashActivity::class.java)
                startActivity(intent1)
            } else {
                setContentView(binding.root)
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )


        return result == PackageManager.PERMISSION_GRANTED
                && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ),
            BluetoothGattCharacteristic.PERMISSION_READ
        )
    }

    private fun showMessageOKCancel(onClickListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@PermissionActivity)
            .setMessage("You need to allow access to both the permissions")
            .setPositiveButton("OK", onClickListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}