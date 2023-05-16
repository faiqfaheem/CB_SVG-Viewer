package svg.viewer.svg.converter.reader.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import svg.viewer.svg.converter.reader.databinding.ActivityConvertedBinding
import java.util.Locale

class ConvertedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConvertedBinding
    private var list = ArrayList<String>()
    private lateinit var adapter: SVGadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConvertedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.includeConvert.txtToolbar.text = getString(R.string.converted_files)
        binding.includeConvert.btnBack.setOnClickListener {
            finish()
        }
        binding.includeConvert.ivSearch.setOnClickListener {
            binding.includeConvert.etxtSearch.visibility = VISIBLE
            binding.includeConvert.txtToolbar.visibility = GONE
            binding.includeConvert.ivCross.visibility = VISIBLE
            binding.includeConvert.ivSearch.visibility = GONE
            showKeyboard(binding.includeConvert.etxtSearch,this@ConvertedActivity)
        }
        binding.includeConvert.ivCross.setOnClickListener {
            binding.includeConvert.etxtSearch.setText("")
            binding.includeConvert.etxtSearch.visibility = GONE
            binding.includeConvert.ivCross.visibility = GONE
            binding.includeConvert.ivSearch.visibility = VISIBLE
            binding.includeConvert.txtToolbar.visibility = VISIBLE
            adapter.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        binding.includeConvert.etxtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        val listfile = this.filesDir.listFiles()
        if (listfile!!.isNotEmpty()) {
            for (file in listfile) { //For each of the entries do:
                if (file.absolutePath.endsWith("pdf") || file.absolutePath.endsWith("png") || file.absolutePath.endsWith("jpg")) {
                    list.add(file.absolutePath)
                }
            }
        }
        if (list.isNotEmpty()) {
            binding.rvConverted.layoutManager = LinearLayoutManager(this)
            adapter = SVGadapter(list, this@ConvertedActivity, 2)
            binding.rvConverted.adapter = adapter
            binding.rvConverted.setHasFixedSize(true)
        } else {
            binding.rvConverted.visibility = GONE
            binding.layoutNoData.visibility = VISIBLE
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