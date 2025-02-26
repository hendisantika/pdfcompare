package com.example.pdfcompare.service;

import org.springframework.web.multipart.MultipartFile;

public interface PDFComparisonService {

    byte[] comparePDFs(MultipartFile pdf1, MultipartFile pdf2, boolean isMultiple) throws Exception;

}
