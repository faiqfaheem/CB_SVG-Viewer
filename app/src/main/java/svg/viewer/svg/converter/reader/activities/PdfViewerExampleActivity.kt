package svg.viewer.svg.converter.reader.activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import svg.viewer.svg.converter.reader.R
import svg.viewer.svg.converter.reader.pdfcreator.activity.PDFViewerActivity

class PdfViewerExampleActivity : PDFViewerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Pdf Viewer"
            supportActionBar!!.setBackgroundDrawable(
                ColorDrawable(
                    resources
                        .getColor(R.color.transparent)
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        //        inflater.inflate(R.menu.menu_pdf_viewer, menu);
        // return true so that the menu pop up is opened
        return true
    } //    @Override
    //    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    //        if (item.getItemId() == android.R.id.home) {
    //            finish();
    //        } else if (item.getItemId() == R.id.menuPrintPdf) {
    //            File fileToPrint = getPdfFile();
    //            if (fileToPrint == null || !fileToPrint.exists()) {
    //                Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show();
    //                return super.onOptionsItemSelected(item);
    //            }
    //
    //            PrintAttributes.Builder printAttributeBuilder = new PrintAttributes.Builder();
    //            printAttributeBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
    //            printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
    //
    //            PDFUtil.printPdf(PdfViewerExampleActivity.this, fileToPrint, printAttributeBuilder.build());
    //        } else if (item.getItemId() == R.id.menuSharePdf) {
    //            File fileToShare = getPdfFile();
    //            if (fileToShare == null || !fileToShare.exists()) {
    //                Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show();
    //                return super.onOptionsItemSelected(item);
    //            }
    //
    //            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
    //
    //            Uri apkURI = FileProvider.getUriForFile(
    //                    getApplicationContext(),
    //                    getApplicationContext()
    //                            .getPackageName() + ".provider", fileToShare);
    //            intentShareFile.setDataAndType(apkURI, URLConnection.guessContentTypeFromName(fileToShare.getName()));
    //            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    //
    //            intentShareFile.putExtra(Intent.EXTRA_STREAM,
    //                    apkURI);
    //
    //            startActivity(Intent.createChooser(intentShareFile, "Share File"));
    //        }
    //        return super.onOptionsItemSelected(item);
    //    }
}