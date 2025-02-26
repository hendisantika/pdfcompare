package com.example.pdfcompare.util;

import com.example.pdfcompare.model.ImageChunk;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PDFImageExtractor {

    public List<ImageChunk> extractImages(PdfReader reader, int pageNum) throws IOException {
        List<ImageChunk> images = new ArrayList<>();
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        // Process the page content with an inline RenderListener.
        parser.processContent(pageNum, new RenderListener() {
            @Override
            public void beginTextBlock() { }

            @Override
            public void endTextBlock() { }

            @Override
            public void renderText(TextRenderInfo renderInfo) { }

            @Override
            public void renderImage(ImageRenderInfo renderInfo) {
                try {
                    PdfImageObject image = renderInfo.getImage();
                    if (image == null) {
                        return;
                    }
                    byte[] imageBytes = image.getImageAsBytes();
                    String imageHash = HashUtilityClass.hashBytes(imageBytes);

                    Matrix ctm = renderInfo.getImageCTM();
                    Vector[] corners = new Vector[4];
                    corners[0] = new Vector(0, 0, 1).cross(ctm);
                    corners[1] = new Vector(0, 1, 1).cross(ctm);
                    corners[2] = new Vector(1, 1, 1).cross(ctm);
                    corners[3] = new Vector(1, 0, 1).cross(ctm);

                    float minX = Float.MAX_VALUE;
                    float minY = Float.MAX_VALUE;
                    float maxX = Float.MIN_VALUE;
                    float maxY = Float.MIN_VALUE;

                    for (Vector corner : corners) {
                        float x = corner.get(Vector.I1);
                        float y = corner.get(Vector.I2);
                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                    }

                    Rectangle rect = new Rectangle(minX, minY, maxX, maxY);
                    images.add(new ImageChunk(imageHash, rect));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return images;
    }
}
