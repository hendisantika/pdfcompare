package com.example.pdfcompare.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PDFPageComparator {

    private final TextComparator textComparator;
    private final ImageComparator imageComparator;
    private final PDFHighlighter pdfHighlighter;
    private final PDFTextExtractor pdfTextExtractor;
    private final PDFImageExtractor pdfImageExtractor;

    public void comparePage(PdfReader reader1, PdfReader reader2, int pageNum, PdfContentByte cb,
                            float width1, float width2, float height1, float height2, boolean isMultiple)
            throws IOException {
        // Highlight entire page if one PDF has an extra page.
        if (pageNum > reader1.getNumberOfPages() && pageNum <= reader2.getNumberOfPages()) {
            Rectangle rect = new Rectangle(0, 0, width2, height2);
            pdfHighlighter.highlightEntirePage(cb, rect, BaseColor.GREEN, isMultiple ? width1 : 0);
            return;
        } else if (pageNum <= reader1.getNumberOfPages() && pageNum > reader2.getNumberOfPages() && isMultiple) {
            Rectangle rect = new Rectangle(0, 0, width1, height1);
            pdfHighlighter.highlightEntirePage(cb, rect, BaseColor.RED, 0);
            return;
        }

        // Compare text differences.
        var words1 = pdfTextExtractor.extractWords(reader1, pageNum);
        var words2 = pdfTextExtractor.extractWords(reader2, pageNum);
        textComparator.compareText(cb, words1, words2, width1, isMultiple);

        // Compare image differences.
        var images1 = pdfImageExtractor.extractImages(reader1, pageNum);
        var images2 = pdfImageExtractor.extractImages(reader2, pageNum);
        imageComparator.compareImages(cb, images1, images2, width1, isMultiple);

    }

}
