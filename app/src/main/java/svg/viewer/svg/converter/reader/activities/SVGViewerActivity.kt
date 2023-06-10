package svg.viewer.svg.converter.reader.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.adapter.SVGadapter
import svg.viewer.svg.converter.reader.databinding.ActivitySvgviewerBinding
import svg.viewer.svg.converter.reader.model.Utils
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SVGViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySvgviewerBinding
    private var pathList = ArrayList<String>()
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private  var adapter: SVGadapter? = null
    private val check = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySvgviewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.includeSvg.txtToolbar.text = getString(R.string.app_name)
        binding.includeSvg.btnBack.setOnClickListener {
            finish()
        }
        binding.includeSvg.ivSearch.setOnClickListener {
            binding.includeSvg.etxtSearch.visibility = VISIBLE
            binding.includeSvg.ivCross.visibility = VISIBLE
            binding.includeSvg.ivSearch.visibility = GONE
            binding.includeSvg.txtToolbar.visibility = GONE
            showKeyboard(binding.includeSvg.etxtSearch,this@SVGViewerActivity)
        }
        binding.includeSvg.ivCross.setOnClickListener {
            binding.includeSvg.etxtSearch.setText("")
            binding.includeSvg.etxtSearch.visibility = GONE
            binding.includeSvg.ivCross.visibility = GONE
            binding.includeSvg.ivSearch.visibility = VISIBLE
            binding.includeSvg.txtToolbar.visibility = VISIBLE
            adapter?.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        binding.includeSvg.etxtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        executor.execute {
            pathList = Utils.getAllSvgFiles(this@SVGViewerActivity)

            handler.post {
                if(pathList.isEmpty())
                {
                    binding.rvSvg.visibility = GONE
                    binding.layoutNoData.visibility = VISIBLE
                    binding.pb.visibility = GONE
                }
                else {
                    binding.rvSvg.layoutManager = LinearLayoutManager(this@SVGViewerActivity)
                    adapter = SVGadapter(pathList, this@SVGViewerActivity, check)
                    binding.rvSvg.adapter = adapter
                    binding.rvSvg.setHasFixedSize(true)
                    binding.pb.visibility = GONE
                }
            }
        }

    }
    private fun filter(text: String) {
        val filterproduct: java.util.ArrayList<String> = java.util.ArrayList<String>()
        for (i in pathList.indices) {
            if (pathList[i].lowercase()
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filterproduct.add(pathList[i])
            }
        }
        if (filterproduct.isEmpty()) {
            val emptyList: java.util.ArrayList<String> = java.util.ArrayList<String>()
            adapter?.filterList(emptyList)
        } else {
            adapter?.filterList(filterproduct)
        }
    }
    private fun showKeyboard(mEtSearch: EditText, context: Context) {
        mEtSearch.requestFocus()
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEtSearch, 0)
    }

}