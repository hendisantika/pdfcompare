package com.example.pdfcompare.util;

import com.example.pdfcompare.base.AbstractBaseServiceTest;
import com.example.pdfcompare.model.ImageChunk;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageComparatorTest extends AbstractBaseServiceTest {

    @InjectMocks
    private ImageComparator imageComparator;

    @Mock
    private PDFHighlighter pdfHighlighter;

    @Mock
    private PdfContentByte cb;

    /**
     * GIVEN a DELETE scenario (images1 has an extra element compared to images2)
     * AND isMultiple is true,
     * WHEN compareImages is called,
     * THEN the extra image from images1 is highlighted in RED with xOffset 0.
     */
    @Test
    void testCompareImages_DeleteDelta_IsMultipleTrue() {
        // GIVEN
        Rectangle rect1 = new Rectangle(0, 0, 100, 100);
        Rectangle rect2 = new Rectangle(100, 100, 200, 200);
        ImageChunk imageChunk1 = new ImageChunk("hash1", rect1);
        ImageChunk imageChunk2 = new ImageChunk("hash2", rect2);
        // images1 has two images, images2 has only the first image.
        List<ImageChunk> images1 = List.of(imageChunk1, imageChunk2);
        List<ImageChunk> images2 = List.of(imageChunk1);
        float width1 = 100f;
        boolean isMultiple = true;

        // WHEN
        imageComparator.compareImages(cb, images1, images2, width1, isMultiple);

        // THEN: DiffUtils.diff will produce a DELETE delta for "image:hash2".
        // The method should call highlightRectangle on the deleted image with RED and xOffset = 0.
        verify(pdfHighlighter, times(1)).highlightRectangle(cb, rect2, BaseColor.RED, 0f);
        verifyNoMoreInteractions(pdfHighlighter);
    }

    /**
     * GIVEN an INSERT scenario (images2 has an extra element compared to images1)
     * AND isMultiple is false,
     * WHEN compareImages is called,
     * THEN the extra image from images2 is highlighted in GREEN with xOffset 0.
     */
    @Test
    void testCompareImages_InsertDelta_IsMultipleFalse() {
        // GIVEN
        Rectangle rect1 = new Rectangle(0, 0, 100, 100);
        Rectangle rect2 = new Rectangle(200, 200, 300, 300);
        ImageChunk imageChunk1 = new ImageChunk("hash1", rect1);
        ImageChunk imageChunk2 = new ImageChunk("hash2", rect2);
        // images1 has one image, images2 has two images.
        List<ImageChunk> images1 = List.of(imageChunk1);
        List<ImageChunk> images2 = List.of(imageChunk1, imageChunk2);
        float width1 = 100f;
        boolean isMultiple = false; // So offset = (isMultiple ? width1 : 0) becomes 0

        // WHEN
        imageComparator.compareImages(cb, images1, images2, width1, isMultiple);

        // THEN: DiffUtils.diff should produce an INSERT delta for "image:hash2".
        // The method should call highlightRectangle on the inserted image with GREEN and xOffset = 0.
        verify(pdfHighlighter, times(1)).highlightRectangle(cb, rect2, BaseColor.GREEN, 0f);
        verifyNoMoreInteractions(pdfHighlighter);
    }

    /**
     * GIVEN a CHANGE scenario (both images exist but have different identifiers)
     * AND isMultiple is true,
     * WHEN compareImages is called,
     * THEN the method highlights the source image in RED (xOffset 0)
     * and the target image in GREEN (xOffset = width1).
     */
    @Test
    void testCompareImages_ChangeDelta_IsMultipleTrue() {
        // GIVEN
        Rectangle rect1 = new Rectangle(0, 0, 100, 100);
        Rectangle rect2 = new Rectangle(50, 50, 150, 150);
        // images1 and images2 have one element each, but with different identifiers.
        ImageChunk imageChunk1 = new ImageChunk("hash1", rect1);
        ImageChunk imageChunk2 = new ImageChunk("hash2", rect2);
        List<ImageChunk> images1 = List.of(imageChunk1);
        List<ImageChunk> images2 = List.of(imageChunk2);
        float width1 = 100f;
        boolean isMultiple = true;

        // WHEN
        imageComparator.compareImages(cb, images1, images2, width1, isMultiple);

        // THEN: DiffUtils.diff should produce a CHANGE delta.
        // For CHANGE, if isMultiple is true, the method highlights the source (from images1) in RED with offset 0
        // and the target (from images2) in GREEN with offset width1.
        verify(pdfHighlighter, times(1)).highlightRectangle(cb, rect1, BaseColor.RED, 0f);
        verify(pdfHighlighter, times(1)).highlightRectangle(cb, rect2, BaseColor.GREEN, width1);
        verifyNoMoreInteractions(pdfHighlighter);
    }

}