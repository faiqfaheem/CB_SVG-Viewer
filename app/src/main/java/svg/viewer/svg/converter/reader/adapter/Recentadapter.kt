package svg.viewer.svg.converter.reader.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.activities.ConvertedViewActivity
import svg.viewer.svg.converter.reader.activities.SVGViewActivity
import svg.viewer.svg.converter.reader.database.AppDatabase
import svg.viewer.svg.converter.reader.database.User
import svg.viewer.svg.converter.reader.database.UserDao
import svg.viewer.svg.converter.reader.databinding.ItemViewBinding
import svg.viewer.svg.converter.reader.model.Utils
import java.io.File

class Recentadapter(private var list: ArrayList<User>, val context: Context) :
    RecyclerView.Adapter<Recentadapter.RecentHolder>() {
    class RecentHolder(val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun filterList(filterllist: ArrayList<User>) {
        list = filterllist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentHolder {
        val binding =
            ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentHolder, position: Int) {
        with(holder)
        {
            val file = File(list[position].fileUri!!)
            binding.tvFilename.text = file.name
            binding.tvFilesize.text = Utils.formatFileSize(file.length())
            itemView.setOnClickListener {
                if (list[position].fileUri!!.contains(context.filesDir.absolutePath)) {
                    if (list[position].fileUri!!.endsWith("pdf")) {
                        Utils.openPdfFile(File(list[position].fileUri!!), context)
                    } else {
                        val intent = Intent(context, ConvertedViewActivity::class.java)
                        intent.putExtra("Path", list[position].fileUri)
                        context.startActivity(intent)
                    }
                } else {
                    val builder = AlertDialog.Builder(context)
                    val view: View = LayoutInflater.from(context).inflate(
                        R.layout.dialog_codeimage, null
                    )
                    builder.setView(view)
                    val alertDialog = builder.create()
                    alertDialog.setCancelable(true)
                    val btnCode: CardView = view.findViewById(R.id.btnCode)
                    val btnImage: CardView = view.findViewById(R.id.btnImage)
                    btnCode.setOnClickListener {
                        val intent = Intent(context, SVGViewActivity::class.java)
                        intent.putExtra("Image", 1)
                        intent.putExtra("Path", list[position].fileUri)
                        context.startActivity(intent)
                        alertDialog.dismiss()
                    }
                    btnImage.setOnClickListener {
                        val intent = Intent(context, SVGViewActivity::class.java)
                        intent.putExtra("Image", 0)
                        intent.putExtra("Path", list[position].fileUri)
                        context.startActivity(intent)
                        alertDialog.dismiss()
                    }
                    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alertDialog.show()
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
                        Utils.shareFile(list[position].fileUri!!, context)
                    }
                    if (menuItem.itemId == R.id.delete) {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Are you sure you want to delete from recent ?")
                        builder.setPositiveButton("Delete") { _, _ ->
                            val userDao: UserDao =
                                AppDatabase.getAppDatabase(context)!!.getUserDao()
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