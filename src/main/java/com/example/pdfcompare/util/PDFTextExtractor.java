package com.example.pdfcompare.util;

import com.example.pdfcompare.model.TextChunk;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PDFTextExtractor {

    public List<TextChunk> extractWords(PdfReader reader, int pageNum) throws IOException {
        List<TextChunk> words = new ArrayList<>();
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        // Process the page content with an inline RenderListener.
        parser.processContent(pageNum, new RenderListener() {
            @Override
            public void beginTextBlock() { }

            @Override
            public void endTextBlock() { }

            @Override
            public void renderText(TextRenderInfo renderInfo) {
                // Delegate to a helper method to process and collect text chunks.
                words.addAll(processText(renderInfo));
            }

            @Override
            public void renderImage(ImageRenderInfo renderInfo) { }
        });
        return words;
    }

    private List<TextChunk> processText(TextRenderInfo renderInfo) {
        List<TextChunk> chunks = new ArrayList<>();
        String text = renderInfo.getText();
        String[] wordArray = text.split("\\s+");
        int wordIndex = 0;
        List<TextRenderInfo> charInfos = renderInfo.getCharacterRenderInfos();

        for (String word : wordArray) {
            // Using isBlank() (available since Java 11) to check for non-empty words.
            if (!word.isBlank()) {
                if (wordIndex < charInfos.size()) {
                    TextRenderInfo firstChar = charInfos.get(wordIndex);
                    // Use Math.min to ensure we don't exceed the bounds of charInfos.
                    int lastCharIndex = Math.min(wordIndex + word.length() - 1, charInfos.size() - 1);
                    TextRenderInfo lastChar = charInfos.get(lastCharIndex);

                    Vector start = firstChar.getBaseline().getStartPoint();
                    Vector end = lastChar.getAscentLine().getEndPoint();

                    float minX = start.get(Vector.I1);
                    float minY = start.get(Vector.I2);
                    float maxX = end.get(Vector.I1);
                    float maxY = end.get(Vector.I2);

                    Rectangle rect = new Rectangle(minX, minY, maxX, maxY);
                    chunks.add(new TextChunk(word, rect));
                    wordIndex = lastCharIndex + 1;
                }
            } else {
                wordIndex++;
            }
        }
        return chunks;
    }
}
