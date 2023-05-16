package svg.viewer.svg.converter.reader.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
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

class Recentadapter(private var list:ArrayList<User>, val context:Context):RecyclerView.Adapter<Recentadapter.RecentHolder>() {
    class RecentHolder(val binding:RowDesignForSvgViewerBinding):RecyclerView.ViewHolder(binding.root)
    fun filterList(filterllist: ArrayList<User>) {
        // below line is to add our filtered
        // list in our course array list.
        list = filterllist
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentHolder {
        val binding = RowDesignForSvgViewerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecentHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentHolder, position: Int) {
        with(holder)
        {
            val file = File(list[position].fileUri!!)
            binding.tvFilename.text = file.name
            binding.tvFilesize.text = Utils.formatFileSize(file.length())
            itemView.setOnClickListener {
                if(list[position].fileUri!!.contains(context.filesDir.absolutePath))
                {
                    if(list[position].fileUri!!.endsWith("pdf"))
                    {
                        Utils.openPdfFile(File(list[position].fileUri!!),context)
                    }
                    else {
                        val intent = Intent(context, ConvertedViewActivity::class.java)
                        intent.putExtra("Path", list[position].fileUri)
                        context.startActivity(intent)
                    }
                }
                else
                {

                    val dialog = Dialog(context, R.style.AppTheme_Dialog)
                    dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.row_design_for_dialog)
                    val btnCode = dialog.findViewById(R.id.btnCode) as CardView
                    val btnImage = dialog.findViewById(R.id.btnImage) as CardView
                    btnCode.setOnClickListener {
                        val intent = Intent(context, SVGViewActivity::class.java)
                        intent.putExtra("Image",1)
                        intent.putExtra("Path", list[position].fileUri)
                        context.startActivity(intent)
                        dialog.dismiss()
                    }
                    btnImage.setOnClickListener {
                        val intent = Intent(context, SVGViewActivity::class.java)
                        intent.putExtra("Image",0)
                        intent.putExtra("Path", list[position].fileUri)
                        context.startActivity(intent)
                        dialog.dismiss()
                    }
                    dialog.show()

                }
            }
            binding.ivMore.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, binding.ivMore)

                val inflater: MenuInflater = popupMenu.menuInflater
                inflater.inflate(R.menu.menu_item, popupMenu.menu)
                val menuItem = popupMenu.menu.findItem(R.id.delete)
                menuItem.title = context.getString(R.string.delete_from_recent)
                popupMenu.setOnMenuItemClickListener { menuItem ->

                    if (menuItem.itemId == R.id.share) {
                        Utils.shareFile(list[position].fileUri!!,context)
                    }
                    if (menuItem.itemId == R.id.delete) {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Are you sure you want to delete from recent ?")
                        builder.setPositiveButton("Delete") { _, _ ->
                            val userDao: UserDao = AppDatabase.getAppDatabase(context)!!.getUserDao()
                            userDao.deletePath(list[position].fileUri!!)
                            list.removeAt(position)
                            notifyDataSetChanged()
                        }
                        builder.setNegativeButton("Cancel", null)
                        builder.show()
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