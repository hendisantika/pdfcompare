package com.example.pdfcompare.service;

import com.example.pdfcompare.base.AbstractBaseServiceTest;
import com.example.pdfcompare.util.PDFComparator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PDFComparisonServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private PDFComparisonServiceImpl service;

    @Mock
    private PDFComparator pdfComparator;

    @Mock
    private MultipartFile pdf1;

    @Mock
    private MultipartFile pdf2;

    @Test
    void testComparePDFs() throws Exception {
        // GIVEN: Two MultipartFiles that return an InputStream with dummy content.
        byte[] dummyData = "dummy".getBytes(StandardCharsets.UTF_8);
        InputStream is1 = new ByteArrayInputStream(dummyData);
        InputStream is2 = new ByteArrayInputStream(dummyData);
        when(pdf1.getInputStream()).thenReturn(is1);
        when(pdf2.getInputStream()).thenReturn(is2);

        // Also, we define the flag.
        boolean isMultiple = true;

        // Stub pdfComparator.comparePDFs(...) to write a known result ("result") to the OutputStream.
        doAnswer(invocation -> {
            OutputStream os = invocation.getArgument(2);
            os.write("result".getBytes(StandardCharsets.UTF_8));
            return null;
        }).when(pdfComparator).comparePDFs(any(InputStream.class), any(InputStream.class), any(OutputStream.class), eq(isMultiple));

        // WHEN: Calling the service method.
        byte[] result = service.comparePDFs(pdf1, pdf2, isMultiple);

        // THEN: The returned byte array should contain "result" and the comparator should be called once.
        assertNotNull(result, "Result should not be null");
        assertEquals("result", new String(result, StandardCharsets.UTF_8), "Output should match expected result");

        verify(pdfComparator, times(1)).comparePDFs(any(InputStream.class), any(InputStream.class), any(OutputStream.class), eq(isMultiple));
    }

}