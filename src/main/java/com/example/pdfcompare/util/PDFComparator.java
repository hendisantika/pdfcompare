package com.example.pdfcompare.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PDFComparator {

    private final PDFPageComparator pageComparator;

    public void comparePDFs(InputStream pdf1InputStream, InputStream pdf2InputStream,
                            OutputStream outputStream, boolean isMultiple)
            throws IOException, DocumentException {

        PdfReader reader1 = new PdfReader(pdf1InputStream);
        PdfReader reader2 = new PdfReader(pdf2InputStream);

        // Setup document with a default page size.
        Rectangle defaultPageSize = PageSize.A4;
        Document document = new Document(defaultPageSize);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();

        int totalPages = Math.max(reader1.getNumberOfPages(), reader2.getNumberOfPages());

        IntStream.rangeClosed(1, totalPages).forEach(pageNumber -> {
            // Calculate combined page size.
            Rectangle pageSize1 = (pageNumber <= reader1.getNumberOfPages()) ? reader1.getPageSize(pageNumber) : null;
            Rectangle pageSize2 = (pageNumber <= reader2.getNumberOfPages()) ? reader2.getPageSize(pageNumber) : null;
            float width1 = (pageSize1 != null) ? pageSize1.getWidth() : 0;
            float width2 = (pageSize2 != null) ? pageSize2.getWidth() : 0;
            float combinedWidth = isMultiple ? width1 + width2 : width2;
            float height1 = (pageSize1 != null) ? pageSize1.getHeight() : 0;
            float height2 = (pageSize2 != null) ? pageSize2.getHeight() : 0;
            float combinedHeight = Math.max(height1, height2);

            if (combinedWidth == 0 || combinedHeight == 0) {
                return; // Skip this page if both pages are missing.
            }

            Rectangle combinedPageSize = new Rectangle(combinedWidth, combinedHeight);
            document.setPageSize(combinedPageSize);
            document.newPage();

            // Import pages from both PDFs.
            PdfImportedPage page1 = null;
            PdfImportedPage page2 = null;
            if (pageNumber <= reader1.getNumberOfPages()) {
                page1 = writer.getImportedPage(reader1, pageNumber);
                if (isMultiple) {
                    cb.addTemplate(page1, 0, 0);
                }
            }
            if (pageNumber <= reader2.getNumberOfPages()) {
                page2 = writer.getImportedPage(reader2, pageNumber);
                cb.addTemplate(page2, isMultiple ? width1 : 0, 0);
            }
            // Delegate per‑page comparison.
            try {
                pageComparator.comparePage(reader1, reader2, pageNumber, cb, width1, width2, height1, height2, isMultiple);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        document.close();
        reader1.close();
        reader2.close();
    }

}
