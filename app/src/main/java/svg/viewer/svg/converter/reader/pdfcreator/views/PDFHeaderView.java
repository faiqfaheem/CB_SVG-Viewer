package svg.viewer.svg.converter.reader.pdfcreator.views;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.io.Serializable;

import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFVerticalView;
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFView;

public class PDFHeaderView extends PDFVerticalView implements Serializable {

    public PDFHeaderView(@NonNull Context context) {
        super(context);
    }

    @Override
    public PDFHeaderView addView(@NonNull PDFView viewToAdd) {
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public LinearLayout getView() {
        return (LinearLayout) super.getView();
    }
}
