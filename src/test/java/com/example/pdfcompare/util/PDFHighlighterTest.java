package com.example.pdfcompare.util;

import com.example.pdfcompare.base.AbstractBaseServiceTest;
import com.itextpdf.text.pdf.PdfContentByte;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfGState;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class PDFHighlighterTest extends AbstractBaseServiceTest {

    @InjectMocks
    private PDFHighlighter pdfHighlighter;

    @Mock
    private PdfContentByte cb;

    @Test
    void testHighlightEntirePage() {
        // GIVEN
        Rectangle rect = new Rectangle(10, 20, 100, 200);
        float xOffset = 50f;
        BaseColor color = BaseColor.GREEN;

        // WHEN: Call highlightEntirePage
        pdfHighlighter.highlightEntirePage(cb, rect, color, xOffset);

        // THEN: Verify the sequence of operations on cb.
        InOrder inOrder = inOrder(cb);
        inOrder.verify(cb).saveState();
        // We can't directly verify the exact PdfGState instance since it's created inside the method,
        // so we use any(PdfGState.class)
        inOrder.verify(cb).setGState(any(PdfGState.class));
        inOrder.verify(cb).setColorFill(eq(color));
        inOrder.verify(cb).rectangle(eq(rect.getLeft() + xOffset), eq(rect.getBottom()), eq(rect.getWidth()), eq(rect.getHeight()));
        inOrder.verify(cb).fill();
        inOrder.verify(cb).restoreState();
    }

    @Test
    void testDrawRectangle() {
        // GIVEN
        Rectangle rect = new Rectangle(5, 15, 50, 60);
        float xOffset = 20f;
        BaseColor color = BaseColor.RED;

        // WHEN: Call drawRectangle
        pdfHighlighter.drawRectangle(cb, rect, color, xOffset);

        // THEN: Verify the sequence of operations on cb.
        InOrder inOrder = inOrder(cb);
        inOrder.verify(cb).saveState();
        inOrder.verify(cb).setGState(any(PdfGState.class)); // opacity 0.3f, but we check type only
        inOrder.verify(cb).setColorFill(eq(color));
        inOrder.verify(cb).rectangle(eq(rect.getLeft() + xOffset), eq(rect.getBottom()), eq(rect.getWidth()), eq(rect.getHeight()));
        inOrder.verify(cb).fill();
        inOrder.verify(cb).restoreState();
    }

    @Test
    void testHighlightRectangle() {
        // GIVEN: highlightRectangle calls drawRectangle internally.
        Rectangle rect = new Rectangle(0, 0, 80, 120);
        float xOffset = 10f;
        BaseColor color = BaseColor.BLUE;

        // WHEN: Call highlightRectangle
        pdfHighlighter.highlightRectangle(cb, rect, color, xOffset);

        // THEN: It should result in the same calls as drawRectangle.
        InOrder inOrder = inOrder(cb);
        inOrder.verify(cb).saveState();
        inOrder.verify(cb).setGState(any(PdfGState.class));
        inOrder.verify(cb).setColorFill(eq(color));
        inOrder.verify(cb).rectangle(eq(rect.getLeft() + xOffset), eq(rect.getBottom()), eq(rect.getWidth()), eq(rect.getHeight()));
        inOrder.verify(cb).fill();
        inOrder.verify(cb).restoreState();
    }

}