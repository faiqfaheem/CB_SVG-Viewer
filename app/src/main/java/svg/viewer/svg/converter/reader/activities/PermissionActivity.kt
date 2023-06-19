package svg.viewer.svg.converter.reader.activities

import android.Manifest
import android.bluetooth.BluetoothGattCharacteristic
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkPermission()) {
                setContentView(binding.root)
            } else {
                val intent = Intent(this@PermissionActivity, SplashActivity::class.java)
                startActivity(intent)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!checkPermission()) {
                    requestPermission()
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
                    val intent = Intent(this@PermissionActivity, SplashActivity::class.java)
                    startActivity(intent)
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

        } else {
            setContentView(binding.root)
        }
    }

    private fun checkPermission(): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            val result2 =
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            return  result2 == PackageManager.PERMISSION_GRANTED
        }
        else {
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
    }

    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
                BluetoothGattCharacteristic.PERMISSION_READ
            )
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
                BluetoothGattCharacteristic.PERMISSION_READ
            )
        }

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