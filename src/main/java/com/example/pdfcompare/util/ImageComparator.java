package com.example.pdfcompare.util;

import com.example.pdfcompare.model.ImageChunk;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.Patch;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ImageComparator {

    private final PDFHighlighter pdfHighlighter;

    public void compareImages(PdfContentByte cb, List<ImageChunk> images1,
                              List<ImageChunk> images2, float width1, boolean isMultiple) {

        List<String> imageIds1 = images1.stream()
                .map(ImageChunk::getIdentifier)
                .toList();

        List<String> imageIds2 = images2.stream()
                .map(ImageChunk::getIdentifier)
                .toList();

        Patch<String> patch = DiffUtils.diff(imageIds1, imageIds2);

        for (var delta : patch.getDeltas()) {

            switch (delta.getType()) {
                case DELETE -> {
                    if (isMultiple) {
                        highlightChunk(delta.getSource(), images1, cb, BaseColor.RED, 0);
                    }
                }
                case INSERT ->
                        highlightChunk(delta.getTarget(), images2, cb, BaseColor.GREEN, isMultiple ? width1 : 0);
                case CHANGE -> {
                    if (isMultiple) {
                        highlightChunk(delta.getSource(), images1, cb, BaseColor.RED, 0);
                    }
                    highlightChunk(delta.getTarget(), images2, cb, BaseColor.GREEN, isMultiple ? width1 : 0);
                }
            }
        }

    }

    private void highlightChunk(Chunk<String> chunk, List<ImageChunk> images,
                                PdfContentByte cb, BaseColor color, float xOffset) {
        IntStream.range(chunk.getPosition(), chunk.getPosition() + chunk.size())
                .filter(j -> j < images.size())
                .forEach(j -> pdfHighlighter.highlightRectangle(cb,
                        images.get(j).rectangle(), color, xOffset));
    }

}
