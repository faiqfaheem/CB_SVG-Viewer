package svg.viewer.svg.converter.reader.activities

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import br.tiagohm.codeview.CodeView
import br.tiagohm.codeview.Language
import br.tiagohm.codeview.Theme
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.database.AppDatabase
import svg.viewer.svg.converter.reader.database.User
import svg.viewer.svg.converter.reader.database.UserDao
import svg.viewer.svg.converter.reader.databinding.ActivitySvgviewBinding
import svg.viewer.svg.converter.reader.pdfcreator.PdfCreatorActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class SVGViewActivity : AppCompatActivity(), CodeView.OnHighlightListener {
    private lateinit var binding: ActivitySvgviewBinding
    private var check = 11
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySvgviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDao: UserDao = AppDatabase.getAppDatabase(this@SVGViewActivity)!!.getUserDao()
        check = intent.getIntExtra("Image", 0)
        val path = intent.getStringExtra("Path")
        if (check == 0) {
            binding.pb.visibility = VISIBLE
            binding.toolbar.txtToolbar.text = File(path!!).name
            binding.txtvCode.visibility = GONE
            val tempList = userDao.getPathCheck(path)
            if (tempList.isEmpty()) {
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
                val builder = AlertDialog.Builder(this@SVGViewActivity)
                val view: View = LayoutInflater.from(this@SVGViewActivity).inflate(
                    R.layout.convert_dialog,
                    findViewById<LinearLayout>(R.id.dialogContainer)
                )
                builder.setView(view)
                val alertDialog = builder.create()
                alertDialog.setCancelable(true)
                val btnjpg: CardView = view.findViewById(R.id.btnconvertJPG)
                val btnPng: CardView = view.findViewById(R.id.btnConvertPNG)
                val btnPdf: CardView = view.findViewById(R.id.btnConvertPDF)
                val etxtFilename: EditText = view.findViewById(R.id.etxtFileName)
                val fileNameWithoutExtension = path.substringBeforeLast(".")
                val fileName = fileNameWithoutExtension.substringAfterLast("/")
                etxtFilename.setText(fileName)
                btnjpg.setOnClickListener {
                    if (etxtFilename.text.trim().toString().isNotEmpty()) {
                        convertImage(
                            binding.ivImage,
                            Bitmap.CompressFormat.JPEG,
                            etxtFilename.text.toString(),
                            ".jpg"
                        )
                        alertDialog.dismiss()
                    } else {
                        Toast.makeText(this, "Please Enter FileName", Toast.LENGTH_SHORT).show()
                    }
                }
                btnPng.setOnClickListener {
                    if (etxtFilename.text.toString().trim().isNotEmpty()) {
                        convertImage(
                            binding.ivImage,
                            Bitmap.CompressFormat.PNG,
                            etxtFilename.text.toString(),
                            ".png"
                        )
                        alertDialog.dismiss()
                    } else {
                        Toast.makeText(this, "Please Enter FileName", Toast.LENGTH_SHORT).show()
                    }
                }
                btnPdf.setOnClickListener {
                    if (etxtFilename.text.toString().trim().isNotEmpty()) {
                        getExternalFilesDir("temp")!!.absolutePath + "/" + etxtFilename.text.toString() + ".pdf"
                        convertSvgToPdf(etxtFilename.text.toString().trim())
                        alertDialog.dismiss()
                    } else {
                        Toast.makeText(this, "Please Enter FileName", Toast.LENGTH_SHORT).show()
                    }
                }
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                alertDialog.show()
            }
        }
        if (check == 1) {
            binding.pb.visibility = VISIBLE
            binding.toolbar.txtToolbar.text = File(path!!).name
            binding.ivImage.visibility = GONE
            binding.btnConvert.visibility = GONE
            val user = User(0, path)
            val tempList = userDao.getPathCheck(path)
            if (tempList.isEmpty()) {
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
        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
        binding.toolbar.ivSearch.visibility = GONE
        binding.toolbar.etxtSearch.visibility = GONE
    }


    private fun convertImage(
        svgImageView: ImageView,
        bitmapFormat: Bitmap.CompressFormat,
        fileName: String,
        fileExtension: String
    ) {
        val width = svgImageView.width
        val height = svgImageView.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        svgImageView.draw(canvas)
        val directory = getExternalFilesDir("temp")
        val currentTime: Date = Calendar.getInstance().time
        val file = File(directory, fileName + fileExtension)
        val outputStream = FileOutputStream(file)
        bitmap.compress(bitmapFormat, 100, outputStream)
        outputStream.close()
        Toast.makeText(this@SVGViewActivity, "Converted Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun convertSvgToPdf(fileName: String) {

        binding.ivImage.setDrawingCacheEnabled(true)
        val bitmap: Bitmap = Bitmap.createBitmap(binding.ivImage.getDrawingCache())

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        startActivity(
            Intent(this@SVGViewActivity, PdfCreatorActivity::class.java)
                .putExtra("img", byteArray).putExtra("FileName", fileName)
        )


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