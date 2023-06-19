package svg.viewer.svg.converter.reader.pdfcreator

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import svg.viewer.svg.converter.reader.pdfcreator.utils.PDFCreator
import svg.viewer.svg.converter.reader.pdfcreator.utils.PDFUtil
import svg.viewer.svg.converter.reader.pdfcreator.views.PDFBody
import svg.viewer.svg.converter.reader.pdfcreator.views.PDFFooterView
import svg.viewer.svg.converter.reader.pdfcreator.views.PDFHeaderView
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFImageView
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFTextView
import java.io.File
import java.util.Locale


class PdfCreatorActivity : PDFCreator() {
    private var bmp:Bitmap? = null
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val extras = intent.extras
        val byteArray = extras!!.getByteArray("img")

        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        val fileName = extras.getString("FileName")
        createPDF(fileName+System.currentTimeMillis(), object : PDFUtil.PDFUtilListener {
            override fun pdfGenerationSuccess(savedPDFFile: File) {
                Toast.makeText(
                    this@PdfCreatorActivity,
                    "PDF Created at$savedPDFFile",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun pdfGenerationFailure(exception: Exception) {
                Toast.makeText(
                    this@PdfCreatorActivity,
                    "PDF NOT Created",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun getHeaderView(pageIndex: Int): PDFHeaderView? {
        return null
    }

    override fun getBodyViews(): PDFBody {
        val pdfBody = PDFBody()
        val pdfImageView = PDFImageView(applicationContext)
        val childLayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            700, Gravity.CENTER
        )
        pdfImageView.setLayout(childLayoutParams)
        pdfImageView.setImageBitmap(bmp!!)
        pdfBody.addView(pdfImageView)
        return pdfBody
    }

    override fun getFooterView(pageIndex: Int): PDFFooterView? {
        val footerView = PDFFooterView(applicationContext)
        val pdfTextViewPage = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1))
        pdfTextViewPage.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f
            )
        )
        pdfTextViewPage.view.gravity = Gravity.CENTER_HORIZONTAL
        footerView.addView(pdfTextViewPage)
        return footerView
    }

    override fun getWatermarkView(forPage: Int): PDFImageView? {
        return null
    }

    override fun onNextClicked(savedPDFFile: File) {
        val pdfUri = Uri.fromFile(savedPDFFile)
        val intentPdfViewer =
            Intent(this@PdfCreatorActivity, PdfViewerActivity::class.java)
        intentPdfViewer.putExtra(PDF_FILE_URI, pdfUri)
        startActivity(intentPdfViewer)
    }

    companion object {
        val PDF_FILE_URI: String = ""
    }
}