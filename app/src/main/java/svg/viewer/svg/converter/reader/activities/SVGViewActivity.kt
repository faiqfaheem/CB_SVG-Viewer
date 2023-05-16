package svg.viewer.svg.converter.reader.activities

import android.app.Dialog
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import br.tiagohm.codeview.CodeView
import br.tiagohm.codeview.Language
import br.tiagohm.codeview.Theme
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.databinding.ActivitySvgviewBinding
import svg.viewer.svg.converter.reader.roomDB.AppDatabase
import svg.viewer.svg.converter.reader.roomDB.User
import svg.viewer.svg.converter.reader.roomDB.UserDao
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class SVGViewActivity : AppCompatActivity() , CodeView.OnHighlightListener {
    private lateinit var binding: ActivitySvgviewBinding
    private var check = 11
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySvgviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userDao: UserDao = AppDatabase.getAppDatabase(this@SVGViewActivity)!!.getUserDao()
        check = intent.getIntExtra("Image",0)
        val path = intent.getStringExtra("Path")
        if(check == 0)
        {
            binding.pb.visibility = VISIBLE
            binding.includeBinviewer.txtToolbar.text = File(path!!).name
            binding.txtvCode.visibility = GONE
            val tempList = userDao.getPathCheck(path)
            if(tempList.isEmpty()) {
                val user = User(0, path)
                userDao.insertAll(user)
            }
           executor.execute {
               val uri = Uri.fromFile(File(path))
               handler.post {
                   binding.ivImage.setImageURI(uri)
                   binding.pb.visibility = GONE
               }
           }
            binding.btnConvert.setOnClickListener {
//
                val dialog = Dialog(this, R.style.AppTheme_Dialog)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.row_design_for_convert_dialogue)
                val btnjpg = dialog.findViewById(R.id.btnconvertJPG) as CardView
                val btnPng = dialog.findViewById(R.id.btnConvertPNG) as CardView
                val btnPdf = dialog.findViewById(R.id.btnConvertPDF) as CardView
                val etxtFilename = dialog.findViewById(R.id.etxtFileName) as EditText
                val fileNameWithoutExtension = path.substringBeforeLast(".")
                val fileName = fileNameWithoutExtension.substringAfterLast("/")
                etxtFilename.setText(fileName)
                btnjpg.setOnClickListener {
                    if(etxtFilename.text.trim().toString().isNotEmpty())
                    {
                            convertImage(binding.ivImage,Bitmap.CompressFormat.JPEG,etxtFilename.text.toString(),".jpg")
                                dialog.dismiss()
                    }
                    else{
                        Toast.makeText(this, "Please Enter FileName", Toast.LENGTH_SHORT).show()
                    }
                }
                btnPng.setOnClickListener {
                    if(etxtFilename.text.toString().trim().isNotEmpty())
                    {
                            convertImage(binding.ivImage,Bitmap.CompressFormat.PNG,etxtFilename.text.toString(),".png")
                                dialog.dismiss()

                    }
                    else{
                        Toast.makeText(this, "Please Enter FileName", Toast.LENGTH_SHORT).show()
                    }
                }
                btnPdf.setOnClickListener {
                    if(etxtFilename.text.toString().trim().isNotEmpty())
                    {
                        binding.pb.visibility = VISIBLE
                            val pdfout = this.filesDir.absolutePath + "/"+ etxtFilename.text.toString() + ".pdf"
                            convertSvgToPdf(File(path), File(pdfout))
                                dialog.dismiss()
                    }
                    else{
                        Toast.makeText(this, "Please Enter FileName", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.show()
            }
        }
        if(check == 1)
        {
            binding.pb.visibility = VISIBLE
            binding.includeBinviewer.txtToolbar.text = File(path!!).name
            binding.ivImage.visibility = GONE
            binding.btnConvert.visibility = GONE
            val user = User(0,path)
            val tempList = userDao.getPathCheck(path)
            if(tempList.isEmpty()) {
                userDao.insertAll(user)
            }
            executor.execute {
                val inputStream = File(path)
                val svgString = inputStream.inputStream().bufferedReader().use { it.readText() }
                handler.post {
                    binding.txtvCode.setOnHighlightListener(this).setOnHighlightListener(this)
                        .setTheme(Theme.AGATE)
                        .setCode(svgString)
                        .setLanguage(Language.KOTLIN)
                        .setWrapLine(true)
                        .setFontSize(14f)
                        .setZoomEnabled(true)
                        .setShowLineNumber(true)
                        .setStartLineNumber(9000)
                        .apply()
                    binding.pb.visibility = GONE
                }
            }
        }
        binding.includeBinviewer.btnBack.setOnClickListener {
            finish()
        }
        binding.includeBinviewer.ivSearch.visibility = GONE
        binding.includeBinviewer.etxtSearch.visibility = GONE
    }
    private fun convertImage(svgImageView: SVGImageView, bitmapFormat: Bitmap.CompressFormat, fileName: String, fileExtension: String)
    {
        val width = svgImageView.width
        val height = svgImageView.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        svgImageView.draw(canvas)
        val directory = this.filesDir
        val currentTime: Date = Calendar.getInstance().time
        val file = File(directory,fileName+fileExtension)
        val outputStream = FileOutputStream(file)
        bitmap.compress(bitmapFormat, 100, outputStream)
        outputStream.close()
        Toast.makeText(this@SVGViewActivity, "Converted Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun convertSvgToPdf(svgFile: File, pdfFile: File) {
        executor.execute {
            val svg = SVG.getFromInputStream(svgFile.inputStream())
            val bitmap = Bitmap.createBitmap(svg.documentWidth.toInt(), svg.documentHeight.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawARGB(0, 0, 0, 0)
            svg.renderToCanvas(canvas)
            val document = Document()
            val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
            document.open()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = Image.getInstance(stream.toByteArray())
            document.add(image)
            handler.post {
                document.close()
                Toast.makeText(this@SVGViewActivity, "PDF Converted", Toast.LENGTH_SHORT).show()
                binding.pb.visibility = GONE
            }
        }

    }

    override fun onStartCodeHighlight() {
        binding.pb.visibility = VISIBLE
    }

    override fun onFinishCodeHighlight() {
        binding.pb.visibility = GONE
    }

    override fun onLanguageDetected(language: Language, relevance: Int) {
        Toast.makeText(this, "language: $language relevance: $relevance", Toast.LENGTH_SHORT).show()
    }

    override fun onFontSizeChanged(sizeInPx: Int) {
        Log.d("TAG", "font-size: " + sizeInPx + "px")
    }

    override fun onLineClicked(lineNumber: Int, content: String) {
        Log.d(TAG, "onLineClicked: line: $lineNumber html: $content")
    }


}