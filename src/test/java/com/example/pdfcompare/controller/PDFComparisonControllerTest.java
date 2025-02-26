package com.example.pdfcompare.controller;

import com.example.pdfcompare.base.AbstractRestControllerTest;
import com.example.pdfcompare.service.PDFComparisonService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PDFComparisonControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    PDFComparisonService pdfComparisonService;

    @Test
    void testComparePDFs() throws Exception {
        // GIVEN: Two MultipartFiles with dummy PDF content and expected comparison result.
        byte[] dummyPdfBytes = "dummy pdf content".getBytes();
        byte[] expectedResult = "result".getBytes();

        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.pdf", MediaType.APPLICATION_PDF_VALUE, dummyPdfBytes);
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.pdf", MediaType.APPLICATION_PDF_VALUE, dummyPdfBytes);
        String isMultipleParam = "true";

        // When the service is invoked, return the expected result.
        when(pdfComparisonService.comparePDFs(any(), any(), eq(true))).thenReturn(expectedResult);

        // WHEN: Perform the multipart POST request.
        MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/pdf/compare")
                        .file(file1)
                        .file(file2)
                        .param("isMultiple", isMultipleParam))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
                .andReturn();

        // THEN: The response content matches the expected result.
        byte[] responseBytes = mvcResult.getResponse().getContentAsByteArray();
        assertThat(responseBytes).isEqualTo(expectedResult);

        // And verify that the service method was called once with the provided files and isMultiple flag.
        verify(pdfComparisonService, times(1)).comparePDFs(any(), any(), eq(true));

    }

}