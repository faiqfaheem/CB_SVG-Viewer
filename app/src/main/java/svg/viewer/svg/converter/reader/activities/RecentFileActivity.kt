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
import svg.viewer.svg.converter.reader.adapter.Recentadapter
import svg.viewer.svg.converter.reader.databinding.ActivityRecentFileBinding
import svg.viewer.svg.converter.reader.roomDB.AppDatabase
import svg.viewer.svg.converter.reader.roomDB.User
import svg.viewer.svg.converter.reader.roomDB.UserDao
import java.util.Locale

class RecentFileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentFileBinding
    private lateinit var list : List<User>
    private lateinit var adapter: Recentadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.includeRecent.txtToolbar.text = getString(R.string.recent_files)
        binding.includeRecent.btnBack.setOnClickListener {
            finish()
        }
        list = ArrayList()

        binding.includeRecent.ivSearch.setOnClickListener {
            binding.includeRecent.etxtSearch.visibility = VISIBLE
            binding.includeRecent.txtToolbar.visibility = GONE
            binding.includeRecent.ivCross.visibility = VISIBLE
            binding.includeRecent.ivSearch.visibility = GONE
            showKeyboard(binding.includeRecent.etxtSearch,this@RecentFileActivity)
        }
        binding.includeRecent.ivCross.setOnClickListener {
            binding.includeRecent.etxtSearch.setText("")
            binding.includeRecent.etxtSearch.visibility = GONE
            binding.includeRecent.ivCross.visibility = GONE
            binding.includeRecent.ivSearch.visibility = VISIBLE
            binding.includeRecent.txtToolbar.visibility = VISIBLE
            adapter.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        binding.includeRecent.etxtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        val userDao: UserDao = AppDatabase.getAppDatabase(this@RecentFileActivity)!!.getUserDao()
        list = userDao.getAllUri()
        if(list.isNotEmpty()) {
            binding.rvRecent.layoutManager = LinearLayoutManager(this@RecentFileActivity)
            adapter = Recentadapter(list as ArrayList<User>, this@RecentFileActivity)
            binding.rvRecent.adapter = adapter

            binding.rvRecent.setHasFixedSize(true)
        }
        else
        {
            binding.rvRecent.visibility = GONE
            binding.layoutNoData.visibility = VISIBLE
        }
    }
    private fun filter(text: String) {
        val filterproduct: java.util.ArrayList<User> = java.util.ArrayList<User>()
        for (i in list.indices) {
            if (list[i].fileUri?.lowercase()
                    !!.contains(text.lowercase(Locale.getDefault()))
            ) {
                filterproduct.add(User(list[i].id,list[i].fileUri!!))
            }
        }
        if (filterproduct.isEmpty()) {
            val emptyList: java.util.ArrayList<User> = java.util.ArrayList<User>()
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