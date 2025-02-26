package com.example.pdfcompare.model;


import com.itextpdf.text.Rectangle;

public record ImageChunk(String imageHash, Rectangle rectangle) {
    public String getIdentifier() {
        return "image:" + imageHash;
    }
}
