package svg.viewer.svg.converter.reader.pdfcreator.views;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFView;

public class PDFBody implements Serializable {

    private final ArrayList<PDFView> childViewList = new ArrayList<>();

    public PDFBody() {
    }

    public PDFBody addView(@NonNull PDFView pdfViewToAdd) {
        if (pdfViewToAdd instanceof PDFTableView) {
            childViewList.addAll(pdfViewToAdd.getChildViewList());
        } else {
            childViewList.add(pdfViewToAdd);
        }
        return this;
    }

    public ArrayList<PDFView> getChildViewList() {
        return childViewList;
    }
}
