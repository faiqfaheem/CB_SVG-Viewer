package svg.viewer.svg.converter.reader.pdfcreator.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.io.Serializable;

import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFCustomView;
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFVerticalView;
import svg.viewer.svg.converter.reader.pdfcreator.views.basic.PDFView;

public class PDFFooterView extends PDFVerticalView implements Serializable {

    public PDFFooterView(@NonNull Context context) {
        super(context);

        PDFCustomView emptySpaceView = new PDFCustomView(context, new View(context), 0, 0, 1);
        this.addView(emptySpaceView);
    }

    @Override
    public PDFFooterView addView(@NonNull PDFView viewToAdd) {
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public LinearLayout getView() {
        return (LinearLayout) super.getView();
    }
}
