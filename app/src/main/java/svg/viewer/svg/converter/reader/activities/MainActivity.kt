package svg.viewer.svg.converter.reader.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import svg.viewer.svg.converter.reader.BuildConfig
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.databinding.ActivityMainBinding
import svg.viewer.svg.converter.reader.databinding.LiscenceDialogBinding

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawerLayout.setViewRotation(GravityCompat.START, 8f)
        binding.drawerLayout.setViewElevation(
            GravityCompat.START, 20f
        )
        binding.drawerLayout.setContrastThreshold(3f)
        binding.drawerLayout.setRadius(
            GravityCompat.START,
            20f
        )
        binding.ivDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.layoutSvgviewer.setOnClickListener {
            val intent = Intent(this@MainActivity, SVGViewerActivity::class.java)
            startActivity(intent)
        }
        binding.layoutRecentFiles.setOnClickListener {
            val intent = Intent(this@MainActivity, RecentFileActivity::class.java)
            startActivity(intent)
        }
        binding.layoutConverted.setOnClickListener {
            val intent = Intent(this@MainActivity, ConvertedActivity::class.java)
            startActivity(intent)
        }
        binding.layoutSvgcode.setOnClickListener {
            val intent = Intent(this@MainActivity, SVGcodeActivity::class.java)
            startActivity(intent)
        }
        binding.navView.setNavigationItemSelectedListener(this@MainActivity)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_privacy -> {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://computerbytes07.blogspot.com/2023/06/svg-viewer.html")
                    )
                )
            }

            R.id.nav_license -> {
                val dialogBinding = LiscenceDialogBinding.inflate(layoutInflater)
                dialog = Dialog(this@MainActivity, R.style.AppTheme_Dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(dialogBinding.root)
                dialog.show()
                binding.drawerLayout.closeDrawers()
            }

            R.id.nav_feedback -> {
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    val recipients = arrayOf("apps.computerbytes@gmail.com")
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.type = "text/html"
                    intent.putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.feedback) + " " + getString(R.string.app_fname)
                    )
                    intent.setPackage("com.google.android.gm")
                    startActivity(Intent.createChooser(intent, "Send mail"))
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            R.id.nav_rate_us -> {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            R.id.nav_moreapps -> {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/developer?id=Computer+Bytes")
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            R.id.nav_share -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey check out this app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
        }
        binding.drawerLayout.closeDrawers()
        return false
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            val builder = AlertDialog.Builder(this@MainActivity)
            val view: View = LayoutInflater.from(this@MainActivity).inflate(
                R.layout.backpress_dialog,
                findViewById<LinearLayout>(R.id.dialogContainer)
            )
            builder.setView(view)
            val alertDialog = builder.create()
            alertDialog.setCancelable(true)
            val yesBtn: Button = view.findViewById(R.id.btnrate_us)
            val noBtn: Button = view.findViewById(R.id.btnexit)
            yesBtn.setOnClickListener {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            noBtn.setOnClickListener {
                finishAffinity()
            }
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()
        }
    }
}