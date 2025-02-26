package com.example.pdfcompare.util;

import com.example.pdfcompare.base.AbstractBaseServiceTest;
import com.example.pdfcompare.model.TextChunk;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TextComparatorTest extends AbstractBaseServiceTest {

    @InjectMocks
    private TextComparator textComparator;

    @Mock
    private PDFHighlighter pdfHighlighter;

    @Mock
    private PdfContentByte cb;

    @Test
    void testCompareText_whenInsertAndDelete_thenHighlightChanges() {
        // GIVEN
        // Two TextChunks in the first PDF
        Rectangle rect1 = new Rectangle(0, 0, 50, 10);
        Rectangle rect2 = new Rectangle(0, 20, 50, 30);
        TextChunk chunk1 = new TextChunk("Hello", rect1);
        TextChunk chunk2 = new TextChunk("World", rect2);
        List<TextChunk> words1 = List.of(chunk1, chunk2);

        // Two TextChunks in the second PDF
        Rectangle rect3 = new Rectangle(0, 0, 50, 10);
        Rectangle rect4 = new Rectangle(0, 20, 50, 30);
        TextChunk chunk3 = new TextChunk("Hello", rect3);
        TextChunk chunk4 = new TextChunk("Mars", rect4);
        List<TextChunk> words2 = List.of(chunk3, chunk4);

        float width1 = 100f;
        boolean isMultiple = true;

        // (Optionally, you can use BDDMockito.given(...) for mocks that return something,
        // but here pdfHighlighter has void methods, so we just verify calls later.)

        // WHEN
        textComparator.compareText(cb, words1, words2, width1, isMultiple);

        // THEN
        // 'World' should be highlighted as RED (delete).
        verify(pdfHighlighter).drawRectangle(cb, rect2, BaseColor.RED, 0f);

        // 'Mars' should be highlighted as GREEN (insert).
        verify(pdfHighlighter).drawRectangle(cb, rect4, BaseColor.GREEN, width1);

        // No other highlights should occur
        verifyNoMoreInteractions(pdfHighlighter);
    }

    /**
     * 1) DELETE scenario with isMultiple=false
     *    => "World" is deleted, but highlight is NOT applied (since isMultiple=false).
     */
    @Test
    void testCompareText_whenDeleteAndIsMultipleFalse_thenNoHighlight() {
        // GIVEN
        // words1 has "Hello", "World"
        Rectangle rect1 = new Rectangle(0, 0, 50, 10);
        Rectangle rect2 = new Rectangle(0, 20, 50, 30);
        TextChunk chunk1 = new TextChunk("Hello", rect1);
        TextChunk chunk2 = new TextChunk("World", rect2);
        List<TextChunk> words1 = List.of(chunk1, chunk2);

        // words2 has only "Hello" => "World" is deleted
        Rectangle rect3 = new Rectangle(0, 0, 50, 10);
        TextChunk chunk3 = new TextChunk("Hello", rect3);
        List<TextChunk> words2 = List.of(chunk3);

        boolean isMultiple = false;
        float width1 = 100f;

        // WHEN
        textComparator.compareText(cb, words1, words2, width1, isMultiple);

        // THEN
        // Because isMultiple=false, the DELETE block won't highlight "World".
        verifyNoInteractions(pdfHighlighter);
    }

    /**
     * 2) DELETE scenario with isMultiple=true
     *    => "World" is deleted, highlight is applied in RED at x=0.
     */
    @Test
    void testCompareText_whenDeleteAndIsMultipleTrue_thenHighlight() {
        // GIVEN
        // words1 has "Hello", "World"
        Rectangle rect1 = new Rectangle(0, 0, 50, 10);
        Rectangle rect2 = new Rectangle(0, 20, 50, 30);
        TextChunk chunk1 = new TextChunk("Hello", rect1);
        TextChunk chunk2 = new TextChunk("World", rect2);
        List<TextChunk> words1 = List.of(chunk1, chunk2);

        // words2 has only "Hello" => "World" is deleted
        Rectangle rect3 = new Rectangle(0, 0, 50, 10);
        TextChunk chunk3 = new TextChunk("Hello", rect3);
        List<TextChunk> words2 = List.of(chunk3);

        boolean isMultiple = true;
        float width1 = 100f;

        // WHEN
        textComparator.compareText(cb, words1, words2, width1, isMultiple);

        // THEN
        // "World" (chunk2) should be highlighted in RED at offset x=0
        verify(pdfHighlighter).drawRectangle(cb, rect2, BaseColor.RED, 0f);
        verifyNoMoreInteractions(pdfHighlighter);
    }

    /**
     * 3) INSERT scenario (with isMultiple=false for demonstration)
     *    => "Mars" is inserted, highlight is applied in GREEN at x=0 (since isMultiple=false).
     */
    @Test
    void testCompareText_whenInsertAndIsMultipleFalse_thenHighlight() {
        // GIVEN
        // words1 has "Hello"
        Rectangle rect1 = new Rectangle(0, 0, 50, 10);
        TextChunk chunk1 = new TextChunk("Hello", rect1);
        List<TextChunk> words1 = List.of(chunk1);

        // words2 has "Hello", "Mars" => "Mars" is inserted
        Rectangle rect2 = new Rectangle(0, 20, 50, 30);
        TextChunk chunk2 = new TextChunk("Hello", rect1); // same rect as chunk1 for "Hello"
        TextChunk chunk3 = new TextChunk("Mars", rect2);
        List<TextChunk> words2 = List.of(chunk2, chunk3);

        boolean isMultiple = false;
        float width1 = 100f;

        // WHEN
        textComparator.compareText(cb, words1, words2, width1, isMultiple);

        // THEN
        // "Mars" is an INSERT, so it should be highlighted in GREEN at x=0
        verify(pdfHighlighter).drawRectangle(cb, rect2, BaseColor.GREEN, 0f);
        verifyNoMoreInteractions(pdfHighlighter);
    }

    /**
     * 4) (Already from the previous example)
     *    Combined DELETE + INSERT scenario with isMultiple=true.
     *    'World' is deleted, 'Mars' is inserted.
     */
    @Test
    void testCompareText_whenDeleteAndInsertAndIsMultipleTrue_thenBothHighlighted() {
        // GIVEN
        Rectangle rect1 = new Rectangle(0, 0, 50, 10);
        Rectangle rect2 = new Rectangle(0, 20, 50, 30);
        TextChunk chunk1 = new TextChunk("Hello", rect1);
        TextChunk chunk2 = new TextChunk("World", rect2);
        List<TextChunk> words1 = List.of(chunk1, chunk2);

        Rectangle rect3 = new Rectangle(0, 0, 50, 10);
        Rectangle rect4 = new Rectangle(0, 20, 50, 30);
        TextChunk chunk3 = new TextChunk("Hello", rect3);
        TextChunk chunk4 = new TextChunk("Mars", rect4);
        List<TextChunk> words2 = List.of(chunk3, chunk4);

        boolean isMultiple = true;
        float width1 = 100f;

        // WHEN
        textComparator.compareText(cb, words1, words2, width1, isMultiple);

        // THEN
        // 'World' is deleted => highlight in RED at x=0
        verify(pdfHighlighter).drawRectangle(cb, rect2, BaseColor.RED, 0f);
        // 'Mars' is inserted => highlight in GREEN at x=width1
        verify(pdfHighlighter).drawRectangle(cb, rect4, BaseColor.GREEN, 100f);

        verifyNoMoreInteractions(pdfHighlighter);
    }

}