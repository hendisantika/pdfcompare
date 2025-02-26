package com.example.pdfcompare.service;

import com.example.pdfcompare.util.PDFComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class PDFComparisonServiceImpl implements PDFComparisonService {

    private final PDFComparator pdfComparator;

    @Override
    public byte[] comparePDFs(MultipartFile pdf1, MultipartFile pdf2, boolean isMultiple) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream is1 = pdf1.getInputStream();
             InputStream is2 = pdf2.getInputStream()) {
            pdfComparator.comparePDFs(is1, is2, outputStream, isMultiple);
        }
        return outputStream.toByteArray();
    }

}
