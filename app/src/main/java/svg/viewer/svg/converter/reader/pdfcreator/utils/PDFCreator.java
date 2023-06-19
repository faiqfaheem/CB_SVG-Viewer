package svg.viewer.svg.converter.reader.pdfcreator.utils;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import svg.viewer.svg.converter.reader.R;
import svg.viewer.svg.converter.reader.pdfcreator.views.PDFBody;
import svg.viewer.svg.converter.reader.pdfcreator.views.PDFFooterView;
import svg.viewer.svg.converter.reader.pdfcreator.views.PDFHeaderView;
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFImageView;
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFPageBreakView;
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFVerticalView;
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFView;

public abstract class PDFCreator extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PDFCreatorActivity";
    LinearLayout layoutPageParent, layoutPrintPreview;
    TextView textViewGeneratingPDFHolder, textViewPageNumber, textViewPreviewNotAvailable;
    AppCompatImageView imageViewPDFPreview;
    Button buttonEmailVisit;
    ImageButton buttonNextPage, buttonPreviousPage;
    ArrayList<Bitmap> pagePreviewBitmapList = new ArrayList<>();
    File savedPDFFile = null;
    private int heightRequiredByHeader = 0;
    private int heightRequiredByFooter = 0;
    private int selectedPreviewPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcreator);

        layoutPageParent = findViewById(R.id.layoutPdfPreview);
        textViewGeneratingPDFHolder = findViewById(R.id.textViewPdfGeneratingHolder);
        layoutPrintPreview = findViewById(R.id.layoutPrintPreview);
        imageViewPDFPreview = layoutPrintPreview.findViewById(R.id.imagePreviewPdf);
        textViewPageNumber = layoutPrintPreview.findViewById(R.id.textViewPreviewPageNumber);
        textViewPreviewNotAvailable = layoutPrintPreview.findViewById(R.id.textViewPreviewPDFNotSupported);

        layoutPageParent.removeAllViews();

        buttonNextPage = layoutPrintPreview.findViewById(R.id.buttonNextPage);
        buttonNextPage.setOnClickListener(this);
        buttonPreviousPage = layoutPrintPreview.findViewById(R.id.buttonPreviousPage);
        buttonPreviousPage.setOnClickListener(this);
    }

    public void createPDF(String fileName, final PDFUtil.PDFUtilListener pdfUtilListener) {
        ArrayList<View> bodyViewList = new ArrayList<>();
        View header = null;
        if (getHeaderView(0) != null) {
            header = getHeaderView(0).getView();
            header.setTag(PDFHeaderView.class.getSimpleName());
            bodyViewList.add(header);
            addViewToTempLayout(layoutPageParent, header);
        }

        if (getBodyViews() != null) {
            for (PDFView pdfView : getBodyViews().getChildViewList()) {
                View bodyView = pdfView.getView();
                if (pdfView instanceof PDFPageBreakView) {
                    bodyView.setTag(PDFPageBreakView.class.getSimpleName());
                } else {
                    bodyView.setTag(PDFBody.class.getSimpleName());
                }
                bodyViewList.add(bodyView);
                addViewToTempLayout(layoutPageParent, bodyView);
            }
        }

        View footer = null;
        PDFFooterView pdfFooterView = getFooterView(0);
        if (pdfFooterView != null && pdfFooterView.getView().getChildCount() > 1) {
            footer = pdfFooterView.getView();
            footer.setTag(PDFFooterView.class.getSimpleName());
            addViewToTempLayout(layoutPageParent, footer);
        }

        createPDFFromViewList(header, footer, bodyViewList, fileName, new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                try {
                    pagePreviewBitmapList.clear();
                    pagePreviewBitmapList.addAll(PDFUtil.pdfToBitmap(savedPDFFile));
                    textViewGeneratingPDFHolder.setVisibility(View.GONE);
                    layoutPrintPreview.setVisibility(View.VISIBLE);
                    selectedPreviewPage = 0;
                    imageViewPDFPreview.setImageBitmap(pagePreviewBitmapList.get(selectedPreviewPage));
                    textViewPageNumber.setText(String.format(Locale.getDefault(), "%d OF %d", selectedPreviewPage + 1, pagePreviewBitmapList.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                    imageViewPDFPreview.setVisibility(View.GONE);
                    textViewPageNumber.setVisibility(View.GONE);
                    buttonNextPage.setVisibility(View.GONE);
                    buttonPreviousPage.setVisibility(View.GONE);
                    textViewPreviewNotAvailable.setVisibility(View.VISIBLE);
                }
                PDFCreator.this.savedPDFFile = savedPDFFile;
                pdfUtilListener.pdfGenerationSuccess(savedPDFFile);
            }
            @Override
            public void pdfGenerationFailure(Exception exception) {
                pdfUtilListener.pdfGenerationFailure(exception);
            }
        });
    }
    private void createPDFFromViewList(final View headerView, final View footerView, @NonNull final ArrayList<View> tempViewList, @NonNull final String filename, final PDFUtil.PDFUtilListener pdfUtilListener) {
        tempViewList.get(tempViewList.size() - 1).post(new Runnable() {
            @Override
            public void run() {
                final FileManager fileManager = FileManager.getInstance();
                final int HEIGHT_ALLOTTED_PER_PAGE = (getResources().getDimensionPixelSize(R.dimen.pdf_height) - (getResources().getDimensionPixelSize(R.dimen.pdf_margin_vertical) * 2));

                runOnUiThread(() -> {
                    final List<View> pdfPageViewList = new ArrayList<>();
                    FrameLayout currentPDFLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, layoutPageParent, false);
                    currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    pdfPageViewList.add(currentPDFLayout);

                    // Add watermark layout
                    PDFView watermarkPDFView = getWatermarkView(0);
                    if (watermarkPDFView != null && watermarkPDFView.getView() != null) {
                        currentPDFLayout.addView(watermarkPDFView.getView());
                    }

                    LinearLayout currentPDFView = new PDFVerticalView(getApplicationContext()).getView();
                    final LinearLayout.LayoutParams verticalPageLayoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    currentPDFView.setLayoutParams(verticalPageLayoutParams);
                    currentPDFLayout.addView(currentPDFView);

                    int currentPageHeight = 0;
                    if (headerView != null) heightRequiredByHeader = headerView.getHeight();
                    if (footerView != null) heightRequiredByFooter = footerView.getHeight();

                    int pageIndex = 1;
                    for (int i = 0; i < tempViewList.size(); i++) {
                        View viewItem = tempViewList.get(i);

                        boolean isPageBreakView = false;
                        if (viewItem.getTag() != null && viewItem.getTag() instanceof String) {
                            isPageBreakView = PDFPageBreakView.class.getSimpleName().equalsIgnoreCase((String) viewItem.getTag());
                        }

                        if (currentPageHeight + viewItem.getHeight() > HEIGHT_ALLOTTED_PER_PAGE) {
                            currentPDFLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, layoutPageParent, false);
                            currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            pdfPageViewList.add(currentPDFLayout);
                            currentPageHeight = 0;

                            // Add watermark layout
                            watermarkPDFView = getWatermarkView(pageIndex);
                            if (watermarkPDFView != null && watermarkPDFView.getView() != null) {
                                currentPDFLayout.addView(watermarkPDFView.getView());
                            }

                            currentPDFView = new PDFVerticalView(getApplicationContext()).getView();
                            currentPDFView.setLayoutParams(verticalPageLayoutParams);
                            currentPDFLayout.addView(currentPDFView);

                            // Add page header again
                            if (heightRequiredByHeader > 0) {
                                // If height is available, only then add header
                                LinearLayout layoutHeader = getHeaderView(pageIndex).getView();
                                addViewToTempLayout(layoutPageParent, layoutHeader);
                                currentPageHeight += heightRequiredByHeader;
                                layoutPageParent.removeView(layoutHeader);
                                currentPDFView.addView(layoutHeader);

                                pageIndex = pageIndex + 1;
                            }
                        }

                        if (!isPageBreakView) {
                            // if not empty view, add
                            currentPageHeight += viewItem.getHeight();

                            layoutPageParent.removeView(viewItem);
                            currentPDFView.addView(viewItem);
                        } else {
                            Log.d(TAG, "run: This is PageBreakView");
                            currentPageHeight = HEIGHT_ALLOTTED_PER_PAGE;
                        }
                        int heightRequiredToAddNextView = 0;
                        boolean shouldAddFooterNow = false;

                        if (tempViewList.size() > i + 1) {
                            View nextViewItem = tempViewList.get(i + 1);
                            heightRequiredToAddNextView = nextViewItem.getHeight();

                            if (currentPageHeight + heightRequiredToAddNextView + heightRequiredByFooter > HEIGHT_ALLOTTED_PER_PAGE) {
                                shouldAddFooterNow = true;
                            }

                        } else
                            shouldAddFooterNow = true;

                        if (isPageBreakView || shouldAddFooterNow) {
                            if (heightRequiredByFooter > 0) {
                                // Footer is NOT prematurely added like header, so we need to subtract 1 from pageIndex
                                LinearLayout layoutFooter = getFooterView(pageIndex - 1).getView();
                                addViewToTempLayout(layoutPageParent, layoutFooter);
                                layoutPageParent.removeView(layoutFooter);
                                currentPDFView.addView(layoutFooter);
                                currentPageHeight = HEIGHT_ALLOTTED_PER_PAGE;
                            }
                        }
                    }

                    PDFUtil.getInstance().generatePDF(pdfPageViewList, fileManager.createTempFileWithName(getApplicationContext(), filename + ".pdf", false).getAbsolutePath(), pdfUtilListener);
                });
            }
        });
    }

    private void addViewToTempLayout(LinearLayout layoutPageParent, View viewToAdd) {
        layoutPageParent.addView(viewToAdd);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonNextPage) {
            if (selectedPreviewPage == pagePreviewBitmapList.size() - 1) {
                return;
            }
            selectedPreviewPage = selectedPreviewPage + 1;
            imageViewPDFPreview.setImageBitmap(pagePreviewBitmapList.get(selectedPreviewPage));
            textViewPageNumber.setText(String.format(Locale.getDefault(), "%d of %d", selectedPreviewPage + 1, pagePreviewBitmapList.size()));
        } else if (v == buttonPreviousPage) {
            if (selectedPreviewPage == 0) {
                return;
            }
            selectedPreviewPage = selectedPreviewPage - 1;
            imageViewPDFPreview.setImageBitmap(pagePreviewBitmapList.get(selectedPreviewPage));
            textViewPageNumber.setText(String.format(Locale.getDefault(), "%d of %d", selectedPreviewPage + 1, pagePreviewBitmapList.size()));
        } else if (v == buttonEmailVisit) {
            onNextClicked(savedPDFFile);
        }
    }

    protected abstract PDFHeaderView getHeaderView(int forPage);

    protected abstract PDFBody getBodyViews();

    protected abstract PDFFooterView getFooterView(int forPage);

    @Nullable
    protected PDFImageView getWatermarkView(int forPage) {
        return null;
    }

    protected abstract void onNextClicked(File savedPDFFile);
}
