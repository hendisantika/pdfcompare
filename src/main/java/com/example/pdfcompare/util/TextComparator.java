package com.example.pdfcompare.util;

import com.example.pdfcompare.model.TextChunk;
import com.github.difflib.patch.AbstractDelta;
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
public class TextComparator {

    private final PDFHighlighter pdfHighlighter;

    public void compareText(PdfContentByte cb, List<TextChunk> words1,
                            List<TextChunk> words2, float width1, boolean isMultiple) {
        // Create immutable lists of text using explicit type declarations.
        List<String> texts1 = words1.stream().map(TextChunk::text).toList();
        List<String> texts2 = words2.stream().map(TextChunk::text).toList();

        Patch<String> patch = DiffUtils.diff(texts1, texts2);
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            switch (delta.getType()) {
                case DELETE -> {
                    if (isMultiple) {
                        highlightChunk(delta.getSource(), words1, cb, BaseColor.RED, 0);
                    }
                }
                case INSERT ->
                        highlightChunk(delta.getTarget(), words2, cb, BaseColor.GREEN, isMultiple ? width1 : 0);
                case CHANGE -> {
                    if (isMultiple) {
                        highlightChunk(delta.getSource(), words1, cb, BaseColor.RED, 0);
                    }
                    highlightChunk(delta.getTarget(), words2, cb, BaseColor.GREEN, isMultiple ? width1 : 0);
                }
            }
        }
    }

    private void highlightChunk(Chunk<String> chunk, List<TextChunk> words,
                                PdfContentByte cb, BaseColor color, float xOffset) {
        IntStream.range(chunk.getPosition(), chunk.getPosition() + chunk.size())
                .filter(j -> j < words.size())
                .forEach(j -> pdfHighlighter.drawRectangle(cb, words.get(j).rectangle(), color, xOffset));
    }

}
