package svg.viewer.svg.converter.reader.activities

import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import svg.viewer.svg.converter.reader.databinding.ActivityConvertedViewBinding
import java.io.File

class ConvertedViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConvertedViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConvertedViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val path = intent.getStringExtra("Path")
        binding.includeView.txtToolbar.text = File(path!!).name
        binding.includeView.btnBack.setOnClickListener {
            finish()
        }
        binding.includeView.ivSearch.visibility = GONE
        binding.includeView.etxtSearch.visibility = GONE
        val imageURi = Uri.parse(path)
        binding.myZoomageView.setImageURI(imageURi)
    }
}