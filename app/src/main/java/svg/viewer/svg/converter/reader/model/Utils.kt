package svg.viewer.svg.converter.reader.model

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class Utils {
    companion object{


        fun sharePdfFile(filepath: String,context: Context)
        {
            val pdfFile =
                File(filepath) // Replace with your file path
            val uri = FileProvider.getUriForFile(
                context,
                "${context.applicationContext.packageName}.provider",
                pdfFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(shareIntent, "Share PDF File"))
        }
        fun openPdfFile(file: File, context: Context) {
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION

            val chooser = Intent.createChooser(intent, "Open PDF file with:")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooser)
        }

        fun getAllSvgFiles(context: Context): ArrayList<String> {
            val svgFiles = ArrayList<String>()
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA
            )
            val selection = "${MediaStore.Files.FileColumns.DATA} LIKE '%.svg'"
            val uri = MediaStore.Files.getContentUri("external")
            val cursor = context.contentResolver.query(uri, projection, selection, null, null)
            cursor?.use {
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                while (it.moveToNext()) {
                    val path = it.getString(dataColumn)
                    if (path.endsWith(".svg")) {
                        svgFiles.add(path)
                    }
                }
            }
            return svgFiles
        }

        fun formatFileSize(size: Long): String {
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            var fileSize = size.toDouble()
            var unitIndex = 0

            while (fileSize > 1024 && unitIndex < units.size - 1) {
                fileSize /= 1024
                unitIndex++
            }

            return String.format("%.1f %s", fileSize, units[unitIndex])
        }
        fun shareFile(filepath: String,context: Context)
        {
            val pdfUri: Uri
            pdfUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context,"${context.applicationContext.packageName}.provider",
                    File(filepath)
                )
            } else {
                Uri.fromFile(File(filepath))
            }
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, pdfUri)
            context.startActivity(Intent.createChooser(share, "Share file"))
        }
        fun shareImage(filepath: String , context: Context)
        {
            val imageFile = File(filepath)

// Get the content URI for the image file
            val imageUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/jpeg"
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
// Grant read permission to the other apps
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
// Start the activity with the share intent
            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(shareIntent)
            }
        }
        fun deleteFile(uri: String,context: Context) {
            val file = File(uri)
            val projection = arrayOf(MediaStore.Files.FileColumns._ID)
            val selection = MediaStore.Files.FileColumns.DATA + " = ?"
            val selectionArgs = arrayOf(file.absolutePath)
            val contentResolver = context.contentResolver
            val c: Cursor? = context.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                null
            )
            if (c!!.moveToFirst()) {
                val id: Long = c.getLong(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val deleteUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
                contentResolver.delete(deleteUri, null, null)
                val nfile = File(uri)
                if (nfile.exists()) {
                    nfile.delete()
                    Toast.makeText(context, "File Delete Successfully", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "File cannot be deleted", Toast.LENGTH_SHORT).show()
            }
            c.close()
        }


    }
}