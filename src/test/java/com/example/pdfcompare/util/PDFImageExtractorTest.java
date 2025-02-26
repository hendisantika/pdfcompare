package com.example.pdfcompare.util;

import com.example.pdfcompare.base.AbstractBaseServiceTest;
import com.example.pdfcompare.model.ImageChunk;
import com.itextpdf.text.pdf.parser.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class PDFImageExtractorTest extends AbstractBaseServiceTest {

    @InjectMocks
    private PDFImageExtractor extractor;

    @Mock
    private ImageRenderInfo mockImageRenderInfo;

    /**
     * Test the public extractImages(...) method when getImage() returns a non-null PdfImageObject.
     *
     * GIVEN a dummy PdfReader with 1 page and a field-level mockImageRenderInfo that:
     * - Returns a non-null PdfImageObject whose getImageAsBytes() returns a controlled byte array.
     * - Returns an identity Matrix for getImageCTM().
     * WHEN processContent() triggers renderImage() with this dummy renderInfo,
     * THEN extractImages() should return one ImageChunk with the expected image hash and rectangle (0, 0, 1, 1).
     */
    @Test
    void testExtractImages_ImageNotNull() throws Exception {
        // Arrange: Prepare a dummy byte array and compute the expected hash.
        byte[] dummyBytes = new byte[] {1, 2, 3};
        String expectedHash = HashUtilityClass.hashBytes(dummyBytes);
        String expectedImageChunkIdentifier = "image:" + expectedHash;

                // Create a dummy PdfImageObject and stub its behavior.
        PdfImageObject dummyImage = mock(PdfImageObject.class);
        when(dummyImage.getImageAsBytes()).thenReturn(dummyBytes);
        // Stub the renderInfo to return the dummy image.
        when(mockImageRenderInfo.getImage()).thenReturn(dummyImage);
        // Stub getImageCTM() to return an identity matrix.
        Matrix identityMatrix = new Matrix();
        when(mockImageRenderInfo.getImageCTM()).thenReturn(identityMatrix);

        // Create a dummy PdfReader with one page.
        PdfReader dummyReader = mock(PdfReader.class);
        when(dummyReader.getNumberOfPages()).thenReturn(1);

        // Use mockConstruction to intercept PdfReaderContentParser creation.
        try (MockedConstruction<PdfReaderContentParser> mocked =
                     mockConstruction(PdfReaderContentParser.class, (mock, context) -> {
                         // When processContent is called, simulate invoking renderImage() with our mockImageRenderInfo.
                         doAnswer(invocation -> {
                             int providedPage = invocation.getArgument(0);
                             RenderListener listener = invocation.getArgument(1);
                             assertEquals(1, providedPage, "Page number should be 1");
                             listener.renderImage(mockImageRenderInfo);
                             return null;
                         }).when(mock).processContent(anyInt(), any(RenderListener.class));
                     })) {
            // Act: Call extractImages.
            List<ImageChunk> result = extractor.extractImages(dummyReader, 1);

            // Assert: Expect one ImageChunk with expected hash and rectangle.
            assertNotNull(result, "Result should not be null.");
            assertEquals(1, result.size(), "Expected one image chunk.");
            ImageChunk chunk = result.get(0);
            assertEquals(expectedImageChunkIdentifier, chunk.getIdentifier(), "Image hash should match expected.");
            // With identity matrix, corners become: (0,0,1), (0,1,1), (1,1,1), (1,0,1) so rectangle is (0, 0, 1, 1)
            Rectangle rect = chunk.rectangle();
            assertEquals(0, rect.getLeft(), 0.001, "Left should be 0");
            assertEquals(0, rect.getBottom(), 0.001, "Bottom should be 0");
            assertEquals(1, rect.getWidth(), 0.001, "Width should be 1");
            assertEquals(1, rect.getHeight(), 0.001, "Height should be 1");
        }

        // Verify that getImage(), getImageAsBytes(), and getImageCTM() were each called.
        verify(mockImageRenderInfo, times(1)).getImage();
        verify(mockImageRenderInfo, times(1)).getImageCTM();
    }

    /**
     * Test the public extractImages(...) method when getImage() returns null.
     *
     * GIVEN a dummy PdfReader with 1 page and a field-level mockImageRenderInfo that:
     * - Returns null for getImage().
     * WHEN processContent() triggers renderImage() with this dummy renderInfo,
     * THEN extractImages() should return an empty list.
     */
    @Test
    void testExtractImages_ImageNull() throws Exception {
        // Arrange: Stub the mockImageRenderInfo to return null for getImage().
        when(mockImageRenderInfo.getImage()).thenReturn(null);

        // Create a dummy PdfReader with one page.
        PdfReader dummyReader = mock(PdfReader.class);
        when(dummyReader.getNumberOfPages()).thenReturn(1);

        // Use mockConstruction to intercept PdfReaderContentParser.
        try (MockedConstruction<PdfReaderContentParser> mocked =
                     mockConstruction(PdfReaderContentParser.class, (mock, context) -> {
                         doAnswer(invocation -> {
                             int providedPage = invocation.getArgument(0);
                             RenderListener listener = invocation.getArgument(1);
                             // Invoke renderImage with our mock (which will return null for getImage())
                             listener.renderImage(mockImageRenderInfo);
                             return null;
                         }).when(mock).processContent(anyInt(), any(RenderListener.class));
                     })) {
            // Act: Call extractImages.
            List<ImageChunk> result = extractor.extractImages(dummyReader, 1);

            // Assert: The result should be an empty list.
            assertNotNull(result, "Result should not be null.");
            assertTrue(result.isEmpty(), "Expected an empty list when image is null.");
        }
        // Verify that getImage() was called on the mock.
        verify(mockImageRenderInfo, times(1)).getImage();
    }

}