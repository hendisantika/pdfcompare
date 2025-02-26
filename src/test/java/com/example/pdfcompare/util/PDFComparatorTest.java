package com.example.pdfcompare.util;

import com.example.pdfcompare.base.AbstractBaseServiceTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class PDFComparatorTest extends AbstractBaseServiceTest {

    @InjectMocks
    private PDFComparator comparator;

    @Mock
    private PDFPageComparator pageComparator;

    /**
     * GIVEN two minimal valid PDFs (each with one page of size 200x200)
     * WHEN comparePDFs is called with isMultiple = true
     * THEN pageComparator.comparePage(...) is invoked with expected parameters,
     *      and the output PDF is generated.
     */
    @Test
    void testComparePDFs_IsMultipleTrue() throws Exception {
        // GIVEN
        byte[] pdfBytes = generateMinimalPDFBytes();
        InputStream pdf1InputStream = new ByteArrayInputStream(pdfBytes);
        InputStream pdf2InputStream = new ByteArrayInputStream(pdfBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean isMultiple = true;

        // WHEN
        comparator.comparePDFs(pdf1InputStream, pdf2InputStream, outputStream, isMultiple);

        // THEN
        // Since our minimal PDF has 1 page with width and height = 200,
        // width1 = width2 = 200, and if isMultiple true, combined width = 400.
        // We expect that pageComparator.comparePage() is invoked once with pageNumber 1.
        verify(pageComparator, times(1))
                .comparePage(any(PdfReader.class), any(PdfReader.class), eq(1),
                        any(), eq(200f), eq(200f), eq(200f), eq(200f), eq(isMultiple));
        // And ensure that some output was written.
        assertTrue(outputStream.size() > 0, "Output PDF should be generated");
    }

    /**
     * GIVEN two minimal valid PDFs (each with one page of size 200x200)
     * WHEN comparePDFs is called with isMultiple = false
     * THEN pageComparator.comparePage(...) is invoked with expected parameters,
     *      and the output PDF is generated.
     */
    @Test
    void testComparePDFs_IsMultipleFalse() throws Exception {
        // GIVEN
        byte[] pdfBytes = generateMinimalPDFBytes();
        InputStream pdf1InputStream = new ByteArrayInputStream(pdfBytes);
        InputStream pdf2InputStream = new ByteArrayInputStream(pdfBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean isMultiple = false;

        // WHEN
        comparator.comparePDFs(pdf1InputStream, pdf2InputStream, outputStream, isMultiple);

        // THEN
        // For isMultiple false, combinedWidth equals width2 (200).
        verify(pageComparator, times(1))
                .comparePage(any(PdfReader.class), any(PdfReader.class), eq(1),
                        any(), eq(200f), eq(200f), eq(200f), eq(200f), eq(isMultiple));
        assertTrue(outputStream.size() > 0, "Output PDF should be generated");
    }

    @Test
    void testComparePDFs_whenPageComparatorThrowsIOException_thenRuntimeException() throws Exception {
        // GIVEN: Generate minimal PDF bytes and wrap them in input streams.
        byte[] pdfBytes = generateMinimalPDFBytes();
        InputStream pdf1InputStream = new ByteArrayInputStream(pdfBytes);
        InputStream pdf2InputStream = new ByteArrayInputStream(pdfBytes);
        OutputStream outputStream = new ByteArrayOutputStream();
        boolean isMultiple = true;

        // Stub pageComparator.comparePage to throw IOException when invoked for page 1.
        doThrow(new IOException("Simulated exception")).when(pageComparator)
                .comparePage(any(PdfReader.class), any(PdfReader.class), eq(1),
                        any(), anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyBoolean());

        // WHEN & THEN: Expect that comparePDFs throws a RuntimeException wrapping the IOException.
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            comparator.comparePDFs(pdf1InputStream, pdf2InputStream, outputStream, isMultiple);
        });
        assertNotNull(thrown.getCause(), "Cause should not be null");
        assertTrue(thrown.getCause() instanceof IOException, "Cause should be an IOException");
        assertEquals("Simulated exception", thrown.getCause().getMessage());
    }

    @Test
    void testComparePDFs_pdf1HasTwoPages_pdf2HasOnePage() throws Exception {
        // pdf1 has 2 pages, pdf2 has 1 page => totalPages = 2
        // For pageNumber=2, pdf2 is null => width2=0 => we still create the second page from pdf1
        byte[] pdf1Bytes = generateMinimalPDFBytes(200, 200, /* pageCount= */ 2);
        byte[] pdf2Bytes = generateMinimalPDFBytes(200, 200, /* pageCount= */ 1);

        try (InputStream pdf1InputStream = new ByteArrayInputStream(pdf1Bytes);
             InputStream pdf2InputStream = new ByteArrayInputStream(pdf2Bytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            boolean isMultiple = true;
            comparator.comparePDFs(pdf1InputStream, pdf2InputStream, outputStream, isMultiple);

            // Page 1: both PDFs have a page => (200, 200, 200, 200)
            verify(pageComparator, times(1))
                    .comparePage(any(PdfReader.class), any(PdfReader.class),
                            eq(1), any(),
                            eq(200f), eq(200f),   // width1, width2
                            eq(200f), eq(200f),   // height1, height2
                            eq(true));

            // Page 2: pdf1 has a page, pdf2 does not => (200, 0, 200, 0)
            verify(pageComparator, times(1))
                    .comparePage(any(PdfReader.class), any(PdfReader.class),
                            eq(2), any(),
                            eq(200f), eq(0f),     // width1=200, width2=0
                            eq(200f), eq(0f),     // height1=200, height2=0
                            eq(true));

            assertTrue(outputStream.size() > 0, "Output PDF should contain at least 2 pages.");
        }
    }


    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------
    /**
     * Helper method to generate minimal valid PDF bytes with one page of size 200x200.
     */
    private byte[] generateMinimalPDFBytes() throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Create a document with page size [0 0 200 200]
        Document document = new Document(new Rectangle(0, 0, 200, 200));
        PdfWriter.getInstance(document, baos);
        document.open();
        // Add minimal content so that there's at least one page.
        document.add(new com.itextpdf.text.Paragraph("Test"));
        document.close();
        return baos.toByteArray();
    }


    /**
     * Overloaded method that generates a PDF with 'pageCount' pages.
     */
    private byte[] generateMinimalPDFBytes(float width, float height, int pageCount) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(new Rectangle(0, 0, width, height));
        PdfWriter.getInstance(document, baos);
        document.open();
        for (int i = 0; i < pageCount; i++) {
            document.add(new com.itextpdf.text.Paragraph("Page " + (i + 1)));
            // Start a new page if not the last
            if (i < pageCount - 1) {
                document.newPage();
            }
        }
        document.close();
        return baos.toByteArray();
    }


    /**
     * Generate an "empty" PDF that has 0 pages.
     * This can be done by opening and closing a Document without adding any pages.
     */
    private byte[] generateEmptyPDFBytes() throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Create and immediately close a Document without writing anything.
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        // No pages added
        document.close();
        return baos.toByteArray();
    }

}