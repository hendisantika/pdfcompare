package com.example.pdfcompare.util;

import com.example.pdfcompare.base.AbstractBaseServiceTest;
import com.example.pdfcompare.model.ImageChunk;
import com.example.pdfcompare.model.TextChunk;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

class PDFPageComparatorTest extends AbstractBaseServiceTest {

    @InjectMocks
    private PDFPageComparator comparator;

    @Mock
    private TextComparator textComparator;

    @Mock
    private ImageComparator imageComparator;

    @Mock
    private PDFHighlighter pdfHighlighter;

    @Mock
    private PDFTextExtractor pdfTextExtractor;

    @Mock
    private PDFImageExtractor pdfImageExtractor;

    @Mock
    private PdfContentByte cb;

    @Mock
    private PdfReader reader1;

    @Mock
    private PdfReader reader2;

    /**
     * GIVEN: pageNum > reader1.getNumberOfPages() and pageNum <= reader2.getNumberOfPages()
     *        and isMultiple is true.
     * WHEN:  comparePage(...) is invoked.
     * THEN:  pdfHighlighter.highlightEntirePage(...) is called with a rectangle of size (width2, height2)
     *        and GREEN color, with xOffset = width1, and no further comparisons are performed.
     */
    @Test
    void testComparePage_ExtraPageInReader2() throws IOException {
        // Arrange
        int pageNum = 3;
        float width1 = 100f;
        float width2 = 150f;
        float height1 = 200f;
        float height2 = 250f;
        boolean isMultiple = true;
        when(reader1.getNumberOfPages()).thenReturn(2);
        when(reader2.getNumberOfPages()).thenReturn(3);

        // Act
        comparator.comparePage(reader1, reader2, pageNum, cb, width1, width2, height1, height2, isMultiple);

        // Assert
        Rectangle expectedRect = new Rectangle(0, 0, width2, height2);
        verify(pdfHighlighter, atLeastOnce()).highlightEntirePage(eq(cb), eq(expectedRect), eq(BaseColor.GREEN), eq(width1));
        verifyNoInteractions(textComparator, imageComparator, pdfTextExtractor, pdfImageExtractor);
    }

    /**
     * GIVEN: pageNum <= reader1.getNumberOfPages() and pageNum > reader2.getNumberOfPages()
     *        and isMultiple is true.
     * WHEN:  comparePage(...) is invoked.
     * THEN:  pdfHighlighter.highlightEntirePage(...) is called with a rectangle of size (width1, height1)
     *        and RED color, with xOffset = 0, and no further comparisons are performed.
     */
    @Test
    void testComparePage_ExtraPageInReader1() throws IOException {
        // Arrange
        int pageNum = 3;
        float width1 = 120f;
        float height1 = 220f;
        float width2 = 100f;
        float height2 = 180f;
        boolean isMultiple = true;
        when(reader1.getNumberOfPages()).thenReturn(3);
        when(reader2.getNumberOfPages()).thenReturn(2);

        // Act
        comparator.comparePage(reader1, reader2, pageNum, cb, width1, width2, height1, height2, isMultiple);

        // Assert
        Rectangle expectedRect = new Rectangle(0, 0, width1, height1);
        verify(pdfHighlighter, atLeastOnce()).highlightEntirePage(eq(cb), eq(expectedRect), eq(BaseColor.RED), eq(0f));
        verifyNoInteractions(textComparator, imageComparator, pdfTextExtractor, pdfImageExtractor);
    }

    /**
     * GIVEN: pageNum is within both readers' page count.
     * WHEN:  comparePage(...) is invoked.
     * THEN:  pdfTextExtractor.extractWords(...) is called on both readers,
     *        textComparator.compareText(...) is called with the extracted text,
     *        and pdfImageExtractor.extractImages(...) and imageComparator.compareImages(...) are called.
     */
    @Test
    void testComparePage_BothPagesExist() throws IOException {
        // Arrange
        int pageNum = 1;
        float width1 = 100f;
        float width2 = 150f;
        float height1 = 200f;
        float height2 = 250f;
        boolean isMultiple = true;
        when(reader1.getNumberOfPages()).thenReturn(3);
        when(reader2.getNumberOfPages()).thenReturn(3);

        // Prepare dummy lists for text and images.
        List<TextChunk> dummyWords1 = List.of(new TextChunk("Hello", new Rectangle(0, 0, 50, 10)));
        List<TextChunk> dummyWords2 = List.of(new TextChunk("Hello", new Rectangle(0, 0, 50, 10)));
        when(pdfTextExtractor.extractWords(reader1, pageNum)).thenReturn(dummyWords1);
        when(pdfTextExtractor.extractWords(reader2, pageNum)).thenReturn(dummyWords2);

        List<ImageChunk> dummyImages1 = List.of(new ImageChunk("image:hash1", new Rectangle(0, 0, 60, 20)));
        List<ImageChunk> dummyImages2 = List.of(new ImageChunk("image:hash1", new Rectangle(0, 0, 60, 20)));
        when(pdfImageExtractor.extractImages(reader1, pageNum)).thenReturn(dummyImages1);
        when(pdfImageExtractor.extractImages(reader2, pageNum)).thenReturn(dummyImages2);

        // Act
        comparator.comparePage(reader1, reader2, pageNum, cb, width1, width2, height1, height2, isMultiple);

        // Assert
        // Verify that textComparator.compareText was called with the extracted words.
        verify(textComparator, times(1)).compareText(cb, dummyWords1, dummyWords2, width1, isMultiple);
        // Verify that imageComparator.compareImages was called with the extracted images.
        verify(imageComparator, times(1)).compareImages(cb, dummyImages1, dummyImages2, width1, isMultiple);
        // Ensure that no highlighting of the entire page occurred in this branch.
        verify(pdfHighlighter, never()).highlightEntirePage(any(), any(), any(), anyFloat());
    }

    // ------------------------------------------------------------------------
// ADDITIONAL TEST #1:
// Covers: pageNum > reader1.getNumberOfPages() && pageNum <= reader2.getNumberOfPages()
//         with isMultiple = false => highlight entire page in GREEN with offset=0
// ------------------------------------------------------------------------
    @Test
    void testComparePage_ExtraPageInReader2_isMultipleFalse() throws IOException {
        // Arrange
        int pageNum = 3;
        float width1 = 100f;
        float width2 = 150f;
        float height1 = 200f;
        float height2 = 250f;
        boolean isMultiple = false; // <== difference from existing test

        // pdf1 has only 2 pages, pdf2 has 3 => triggers the first if-condition
        when(reader1.getNumberOfPages()).thenReturn(2);
        when(reader2.getNumberOfPages()).thenReturn(3);

        // Act
        comparator.comparePage(reader1, reader2, pageNum, cb, width1, width2, height1, height2, isMultiple);

        // Assert
        // Expect highlightEntirePage in GREEN with offset=0 (since isMultiple=false)
        Rectangle expectedRect = new Rectangle(0, 0, width2, height2);
        verify(pdfHighlighter).highlightEntirePage(eq(cb), eq(expectedRect), eq(BaseColor.GREEN), eq(0f));
        // No text/image extraction or comparison should occur
        verifyNoInteractions(textComparator, imageComparator, pdfTextExtractor, pdfImageExtractor);
    }

    // ------------------------------------------------------------------------
// ADDITIONAL TEST #2:
// Covers: pageNum <= reader1.getNumberOfPages() && pageNum > reader2.getNumberOfPages() && isMultiple=false
//         => falls through to else block => text & image comparisons
// ------------------------------------------------------------------------
    @Test
    void testComparePage_ExtraPageInReader1_isMultipleFalse_goesToElseBlock() throws IOException {
        // Arrange
        int pageNum = 3;
        float width1 = 120f;
        float height1 = 220f;
        float width2 = 100f;
        float height2 = 180f;
        boolean isMultiple = false; // <== difference from existing test

        // pdf1 has 3 pages, pdf2 has 2 => pageNum=3 is valid for reader1 but beyond reader2
        // Because isMultiple=false, we do NOT highlight the entire page in red
        // and instead fall through to the else block (text/image comparison).
        when(reader1.getNumberOfPages()).thenReturn(3);
        when(reader2.getNumberOfPages()).thenReturn(2);

        // Prepare dummy lists for text and images
        List<TextChunk> dummyWords1 = List.of(new TextChunk("Hello", new Rectangle(0, 0, 50, 10)));
        List<TextChunk> dummyWords2 = List.of(); // Suppose pdf2 has fewer words or none
        when(pdfTextExtractor.extractWords(reader1, pageNum)).thenReturn(dummyWords1);
        when(pdfTextExtractor.extractWords(reader2, pageNum)).thenReturn(dummyWords2);

        List<ImageChunk> dummyImages1 = List.of(new ImageChunk("image:hash1", new Rectangle(0, 0, 60, 20)));
        List<ImageChunk> dummyImages2 = List.of();
        when(pdfImageExtractor.extractImages(reader1, pageNum)).thenReturn(dummyImages1);
        when(pdfImageExtractor.extractImages(reader2, pageNum)).thenReturn(dummyImages2);

        // Act
        comparator.comparePage(reader1, reader2, pageNum, cb, width1, width2, height1, height2, isMultiple);

        // Assert
        // Since the second if-condition requires isMultiple=true to highlight in red,
        // and we have isMultiple=false, we expect text & image comparisons in the else block:
        verify(pdfTextExtractor).extractWords(reader1, pageNum);
        verify(pdfTextExtractor).extractWords(reader2, pageNum);
        verify(textComparator).compareText(cb, dummyWords1, dummyWords2, width1, false);

        verify(pdfImageExtractor).extractImages(reader1, pageNum);
        verify(pdfImageExtractor).extractImages(reader2, pageNum);
        verify(imageComparator).compareImages(cb, dummyImages1, dummyImages2, width1, false);

        // Ensure no "entire page" highlighting
        verify(pdfHighlighter, never()).highlightEntirePage(any(), any(), any(), anyFloat());
    }


}