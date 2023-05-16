package svg.viewer.svg.converter.reader.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import androidx.core.view.GravityCompat
import svg.viewer.svg.converter.reader.BuildConfig
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSetDrawer()


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
    private fun mSetDrawer() {
        binding.drawerLayout.setViewRotation(GravityCompat.START, 8f)
        binding.drawerLayout.setViewElevation(
            GravityCompat.START, 20f
        )
        binding.drawerLayout.setContrastThreshold(3f)
        binding.drawerLayout.setRadius(
            GravityCompat.START,
            20f
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_privacy) {
            try {
                val url =
                    "https://cergisapps.blogspot.com/2023/01/apps-our-privacy-policy-elaborate-how.html"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (item.itemId == R.id.nav_feedback) {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                val recipients = arrayOf("apps.cergis@gmail.com")
                intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                intent.type = "text/html"
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.feedback) + " " + getString(R.string.app_name)
                )
                intent.setPackage("com.google.android.gm")
                startActivity(Intent.createChooser(intent, "Send mail"))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (item.itemId == R.id.nav_rate_us) {
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
        if (item.itemId == R.id.nav_moreapps) {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=Cergis+Apps")
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (item.itemId == R.id.nav_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey check out this app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        return false
    }
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            val dialog = Dialog(this, R.style.AppTheme_Dialog)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.row_deisgn_for_back_pressed)
            val yesBtn = dialog.findViewById(R.id.btnrate_us) as Button
            val noBtn = dialog.findViewById(R.id.btnexit) as Button
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
            dialog.show()
        }
    }
}