package svg.viewer.svg.converter.reader.activities

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.adapter.SVGadapter
import svg.viewer.svg.converter.reader.databinding.ActivitySvgcodeBinding
import svg.viewer.svg.converter.reader.model.Utils
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SVGcodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySvgcodeBinding
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var list = ArrayList<String>()
    private lateinit var adapter: SVGadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySvgcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.includeSvgCode.txtToolbar.text = getString(R.string.svg_code)
        binding.includeSvgCode.btnBack.setOnClickListener {
            finish()
        }
        binding.includeSvgCode.ivSearch.setOnClickListener {
            binding.includeSvgCode.etxtSearch.visibility = VISIBLE
            binding.includeSvgCode.txtToolbar.visibility = GONE
            binding.includeSvgCode.ivCross.visibility = VISIBLE
            binding.includeSvgCode.ivSearch.visibility = GONE
            showKeyboard(binding.includeSvgCode.etxtSearch,this@SVGcodeActivity)
        }
        binding.includeSvgCode.ivCross.setOnClickListener {
            binding.includeSvgCode.etxtSearch.setText("")
            binding.includeSvgCode.etxtSearch.visibility = GONE
            binding.includeSvgCode.ivCross.visibility = GONE
            binding.includeSvgCode.ivSearch.visibility = VISIBLE
            binding.includeSvgCode.txtToolbar.visibility = VISIBLE
            adapter.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        binding.includeSvgCode.etxtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        executor.execute {
            list = Utils.getAllSvgFiles(this@SVGcodeActivity)
            handler.post {
                if(list.isNotEmpty()) {
                    binding.rvSvgCode.layoutManager = LinearLayoutManager(this)
                    adapter = SVGadapter(list, this@SVGcodeActivity, 1)
                    binding.rvSvgCode.adapter = adapter
                    binding.rvSvgCode.setHasFixedSize(true)
                    binding.pb.visibility = GONE
                }
                else
                {
                    binding.rvSvgCode.visibility = GONE
                    binding.layoutNoData.visibility = VISIBLE
                    binding.pb.visibility = GONE
                }
            }
        }

    }
    private fun filter(text: String) {
        val filterproduct: java.util.ArrayList<String> = java.util.ArrayList<String>()
        for (i in list.indices) {
            if (list[i].lowercase()
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filterproduct.add(list[i])
            }
        }
        if (filterproduct.isEmpty()) {
            val emptyList: java.util.ArrayList<String> = java.util.ArrayList<String>()
            adapter.filterList(emptyList)
        } else {
            adapter.filterList(filterproduct)
        }
    }
    private fun showKeyboard(mEtSearch: EditText, context: Context) {
        mEtSearch.requestFocus()
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEtSearch, 0)
    }
}