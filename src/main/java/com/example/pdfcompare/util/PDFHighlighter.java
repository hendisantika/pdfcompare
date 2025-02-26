package com.example.pdfcompare.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import org.springframework.stereotype.Component;

@Component
public class PDFHighlighter {

    public void highlightEntirePage(PdfContentByte cb, Rectangle rect, BaseColor color, float xOffset) {
        cb.saveState();
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.2f);
        cb.setGState(gs);
        cb.setColorFill(color);
        cb.rectangle(rect.getLeft() + xOffset, rect.getBottom(), rect.getWidth(), rect.getHeight());
        cb.fill();
        cb.restoreState();
    }

    public void drawRectangle(PdfContentByte cb, Rectangle rect, BaseColor color, float xOffset) {
        cb.saveState();
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.3f);
        cb.setGState(gs);
        cb.setColorFill(color);
        cb.rectangle(rect.getLeft() + xOffset, rect.getBottom(), rect.getWidth(), rect.getHeight());
        cb.fill();
        cb.restoreState();
    }

    public void highlightRectangle(PdfContentByte cb, Rectangle rect, BaseColor color, float xOffset) {
        drawRectangle(cb, rect, color, xOffset);
    }

}
