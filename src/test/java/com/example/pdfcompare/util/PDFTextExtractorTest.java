package com.example.pdfcompare.util;

import com.example.pdfcompare.base.AbstractBaseServiceTest;
import com.example.pdfcompare.model.TextChunk;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PDFTextExtractorTest extends AbstractBaseServiceTest {

    @InjectMocks
    private PDFTextExtractor extractor;

    @Mock
    private TextRenderInfo mockTextRenderInfo;


    @Test
    void testExtractWords_HelloWorld_public() throws Exception {
        // Arrange: Stub the field-level mock to return "Hello World"
        when(mockTextRenderInfo.getText()).thenReturn("Hello World");

        // Prepare a controlled list of 10 dummy character render infos (simulate 5 for "Hello" and 5 for "World")
        List<TextRenderInfo> charInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TextRenderInfo charMock = mock(TextRenderInfo.class);
            Vector dummyVector = new Vector(i, i, 1);
            LineSegment dummySegment = new LineSegment(dummyVector, dummyVector);
            when(charMock.getBaseline()).thenReturn(dummySegment);
            when(charMock.getAscentLine()).thenReturn(dummySegment);
            charInfos.add(charMock);
        }
        when(mockTextRenderInfo.getCharacterRenderInfos()).thenReturn(charInfos);

        int pageNum = 1;
        // Create a dummy PdfReader (its behavior is not used because we intercept the parser creation)
        PdfReader dummyReader = mock(PdfReader.class);

        // Use Mockito.mockConstruction to intercept the creation of PdfReaderContentParser.
        try (MockedConstruction<PdfReaderContentParser> mockedConstruction =
                     mockConstruction(PdfReaderContentParser.class, (mock, context) -> {
                         // When processContent(...) is called, simulate invoking the RenderListener callback.
                         doAnswer(invocation -> {
                             int providedPage = invocation.getArgument(0);
                             RenderListener listener = invocation.getArgument(1);
                             assertEquals(pageNum, providedPage, "Provided page number should match");
                             // Call renderText on the listener with our field-level mock.
                             listener.renderText(mockTextRenderInfo);
                             return null;
                         }).when(mock).processContent(anyInt(), any(RenderListener.class));
                     })) {
            // Act: Call the public extractWords method.
            List<TextChunk> result = extractor.extractWords(dummyReader, pageNum);

            // Assert: Verify that exactly two TextChunks ("Hello" and "World") are returned.
            assertNotNull(result, "Result should not be null.");
            assertEquals(2, result.size(), "Expected 2 tokens for 'Hello World'.");
            assertEquals("Hello", result.get(0).text(), "First token should be 'Hello'.");
            assertEquals("World", result.get(1).text(), "Second token should be 'World'.");

            // Additionally, verify that processContent was called exactly once with the expected page number.
            List<PdfReaderContentParser> constructedParsers = mockedConstruction.constructed();
            assertEquals(1, constructedParsers.size(), "Expected one PdfReaderContentParser to be constructed.");
            verify(constructedParsers.get(0), times(1)).processContent(eq(pageNum), any(RenderListener.class));
        }
    }

    @Test
    void testProcessText_HelloWorld() throws Exception {
        // WHEN: Stub the mock to return "Hello World"
        when(mockTextRenderInfo.getText()).thenReturn("Hello World");

        // Prepare a controlled list of 10 dummy character render infos:
        // 5 for "Hello" and 5 for "World"
        List<TextRenderInfo> charInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TextRenderInfo charMock = mock(TextRenderInfo.class);
            Vector dummyVector = new Vector(i, i, 1);
            LineSegment dummySegment = new LineSegment(dummyVector, dummyVector);
            when(charMock.getBaseline()).thenReturn(dummySegment);
            when(charMock.getAscentLine()).thenReturn(dummySegment);
            charInfos.add(charMock);
        }
        when(mockTextRenderInfo.getCharacterRenderInfos()).thenReturn(charInfos);

        // WHEN: Invoke the private processText method via reflection.
        Method processTextMethod = PDFTextExtractor.class.getDeclaredMethod("processText", TextRenderInfo.class);
        processTextMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<TextChunk> result = (List<TextChunk>) processTextMethod.invoke(extractor, mockTextRenderInfo);

        // THEN: Verify that two text chunks are returned with texts "Hello" and "World".
        assertNotNull(result, "Result should not be null.");
        assertEquals(2, result.size(), "Expected 2 text chunks for 'Hello World'.");
        assertEquals("Hello", result.get(0).text(), "The first chunk should be 'Hello'.");
        assertEquals("World", result.get(1).text(), "The second chunk should be 'World'.");

        // Verify that getText() and getCharacterRenderInfos() are each called once.
        verify(mockTextRenderInfo, times(1)).getText();
        verify(mockTextRenderInfo, times(1)).getCharacterRenderInfos();
    }

    @Test
    void testProcessText_Whitespace() throws Exception {
        // WHEN: Stub the mock to return only whitespace.
        when(mockTextRenderInfo.getText()).thenReturn("    ");
        when(mockTextRenderInfo.getCharacterRenderInfos()).thenReturn(new ArrayList<>());

        // WHEN: Invoke processText via reflection.
        Method processTextMethod = PDFTextExtractor.class.getDeclaredMethod("processText", TextRenderInfo.class);
        processTextMethod.setAccessible(true);

        List<TextChunk> result = (List<TextChunk>) processTextMethod.invoke(extractor, mockTextRenderInfo);

        // THEN: Verify that an empty list is returned.
        assertNotNull(result, "Result should not be null.");
        assertTrue(result.isEmpty(), "Expected an empty list when input is only whitespace.");

        // Verify that getText() and getCharacterRenderInfos() are each called once.
        verify(mockTextRenderInfo, times(1)).getText();
        verify(mockTextRenderInfo, times(1)).getCharacterRenderInfos();

    }

}