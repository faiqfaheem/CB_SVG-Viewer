package svg.viewer.svg.converter.reader.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.activities.ConvertedViewActivity
import svg.viewer.svg.converter.reader.activities.SVGViewActivity
import svg.viewer.svg.converter.reader.databinding.RowDesignForSvgViewerBinding
import svg.viewer.svg.converter.reader.model.Utils
import svg.viewer.svg.converter.reader.roomDB.AppDatabase
import svg.viewer.svg.converter.reader.roomDB.User
import svg.viewer.svg.converter.reader.roomDB.UserDao
import java.io.File

class SVGadapter(private var list:ArrayList<String>, val context:Context, private val value:Int):RecyclerView.Adapter<SVGadapter.SVGHolder>() {
    class SVGHolder(val binding:RowDesignForSvgViewerBinding):RecyclerView.ViewHolder(binding.root)

    fun filterList(filterllist: ArrayList<String>) {
        // below line is to add our filtered
        // list in our course array list.
        list = filterllist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SVGHolder {
        val binding = RowDesignForSvgViewerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SVGHolder(binding)
    }

    override fun onBindViewHolder(holder: SVGHolder, position: Int) {
        with(holder)
        {
            if(value == 2)
            {
                if(list[position].endsWith("pdf"))
                {
                    binding.ivFile.setImageResource(R.drawable.ic_pdf)
                }
                if(list[position].endsWith("jpg"))
                {
                    binding.ivFile.setImageResource(R.drawable.ic_jpg)
                }
                if (list[position].endsWith("png"))
                {
                    binding.ivFile.setImageResource(R.drawable.ic_png)
                }
                val file = File(list[position])
                binding.tvFilename.text = file.name
                binding.tvFilesize.text = Utils.formatFileSize(file.length())
            }
            if(value == 0 || value == 1) {
                val file = File(list[position])
                binding.tvFilename.text = file.name
                binding.tvFilesize.text = Utils.formatFileSize(file.length())
            }
            itemView.setOnClickListener {
                if(value == 0) {
                    val intent = Intent(context, SVGViewActivity::class.java)
                    intent.putExtra("Image",value)
                    intent.putExtra("Path", list[position])
                    context.startActivity(intent)
                }
                if(value == 1)
                {
                    val intent = Intent(context, SVGViewActivity::class.java)
                    intent.putExtra("Image",value)
                    intent.putExtra("Path", list[position])
                    context.startActivity(intent)
                }
                if(value == 2)
                {
                    val userDao: UserDao = AppDatabase.getAppDatabase(context)!!.getUserDao()
                    val tempList = userDao.getPathCheck(list[position])
                    if(tempList.isEmpty())
                    {
                        val user = User(0,list[position])
                        userDao.insertAll(user)
                    }
                    if(list[position].endsWith("pdf"))
                    {
                        Utils.openPdfFile(File(list[position]),context)
                    }
                    else {
                        val intent = Intent(context, ConvertedViewActivity::class.java)
                        intent.putExtra("Path", list[position])
                        context.startActivity(intent)
                    }
                }
            }
            binding.ivMore.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, binding.ivMore)

                val inflater: MenuInflater = popupMenu.menuInflater
                inflater.inflate(R.menu.menu_item, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    if (menuItem.itemId == R.id.share) {
                        if(value == 0)
                        {
                            Utils.shareFile(list[position],context)
                        }
                        if(value == 1)
                        {
                            Utils.shareFile(list[position],context)
                        }
                        if(value == 2)
                        {
                            if(list[position].endsWith("pdf"))
                            {
                                Utils.sharePdfFile(list[position],context)
                            }
                            else {
                                Utils.shareImage(list[position], context)
                            }
                        }
                    }
                    if (menuItem.itemId == R.id.delete) {
                        if(value == 0)
                        {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure you want to delete ?")
                            builder.setPositiveButton("Delete") { _, _ ->
                                Utils.deleteFile(list[position] , context)
                                list.removeAt(position)
                                notifyDataSetChanged()
                            }
                            builder.setNegativeButton("Cancel", null)
                            builder.show()

                        }
                        if(value == 1)
                        {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure you want to delete ?")
                            builder.setPositiveButton("Delete") { _, _ ->
                                Utils.deleteFile(list[position] , context)
                                list.removeAt(position)
                                notifyDataSetChanged()
                            }
                            builder.setNegativeButton("Cancel", null)
                            builder.show()
                        }
                        if(value == 2)
                        {
                            val filePack = File(context.filesDir, File(list[position]).name)
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure you want to delete ?")
                            builder.setPositiveButton("Delete") { _, _ ->
                                if (filePack.exists()) {
                                    // File was successfully deleted
                                    context.deleteFile(filePack.name)
                                    Toast.makeText(context, "File delete Successfully", Toast.LENGTH_SHORT)
                                        .show()
                                    list.removeAt(position)
                                    notifyDataSetChanged()
                                } else {
                                    Toast.makeText(context, "File not delete", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            builder.setNegativeButton("Cancel", null)
                            builder.show()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }

                popupMenu.show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}