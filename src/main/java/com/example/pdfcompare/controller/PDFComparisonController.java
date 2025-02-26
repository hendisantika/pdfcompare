package com.example.pdfcompare.controller;

import com.example.pdfcompare.service.PDFComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/pdf")
@RequiredArgsConstructor
public class PDFComparisonController {

    private final PDFComparisonService pdfComparisonService;

    @PostMapping(value = "/compare", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> comparePDFs(@RequestParam("file1") MultipartFile file1,
                                              @RequestParam("file2") MultipartFile file2,
                                              @RequestParam(name = "isMultiple", defaultValue = "false") boolean isMultiple) throws Exception {
        byte[] result = pdfComparisonService.comparePDFs(file1, file2, isMultiple);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "comparison.pdf");
        return ResponseEntity.ok().headers(headers).body(result);
    }

}
