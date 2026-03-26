package com.smartarchive.archivemanage.service.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArchiveFileTextExtractor {
    private static final Pattern STYLE_BLOCK = Pattern.compile("(?is)<style[^>]*>.*?</style>");
    private static final Pattern SCRIPT_BLOCK = Pattern.compile("(?is)<script[^>]*>.*?</script>");
    private static final Pattern HTML_TAG = Pattern.compile("(?is)<[^>]+>");
    private static final Pattern HTML_COMMENT = Pattern.compile("(?is)<!--.*?-->");

    private final PdfOcrService pdfOcrService;

    public ExtractedText extract(Path filePath, String fileName, String mimeType) throws IOException {
        String extension = extractExtension(fileName);
        return switch (extension) {
            case "pdf" -> extractPdf(filePath);
            case "docx" -> new ExtractedText(normalizeExtractedText(extractDocx(filePath)), false, "DOCX");
            case "doc" -> new ExtractedText(normalizeExtractedText(extractDoc(filePath)), false, "DOC");
            case "txt", "md", "json", "csv", "xml", "sql", "log", "yml", "yaml", "properties", "java", "ts", "js", "vue", "html", "htm" -> new ExtractedText(normalizeExtractedText(Files.readString(filePath, StandardCharsets.UTF_8)), false, "TEXT");
            default -> mimeType != null && mimeType.startsWith("text/")
                ? new ExtractedText(normalizeExtractedText(Files.readString(filePath, StandardCharsets.UTF_8)), false, "TEXT")
                : new ExtractedText("", false, "UNSUPPORTED");
        };
    }

    private ExtractedText extractPdf(Path filePath) throws IOException {
        String extracted;
        String sortedExtracted;
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper defaultStripper = new PDFTextStripper();
            extracted = defaultStripper.getText(document);
            PDFTextStripper sortedStripper = new PDFTextStripper();
            sortedStripper.setSortByPosition(true);
            sortedExtracted = sortedStripper.getText(document);
        }

        String bestPdfText = chooseBetterPdfText(extracted, sortedExtracted);
        if (!pdfOcrService.isAvailable()) {
            return new ExtractedText(normalizeExtractedText(bestPdfText), false, "PDF");
        }
        if (!shouldSupplementWithOcr(bestPdfText) && hasUsableText(bestPdfText)) {
            return new ExtractedText(normalizeExtractedText(bestPdfText), false, "PDF");
        }

        try {
            String ocrText = pdfOcrService.extractPdf(filePath);
            String mergedText = mergePdfAndOcrText(bestPdfText, ocrText);
            String finalText = normalizeExtractedText(chooseBestFinalText(bestPdfText, mergedText, ocrText));
            return new ExtractedText(finalText, hasUsableText(ocrText), "PDF_OCR");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("OCR interrupted", exception);
        }
    }

    private String extractDocx(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = normalizeExtractedText(extractor.getText());
            if (hasUsableText(text)) {
                return text;
            }
        }
        return extractDocxXmlFallback(filePath);
    }

    private String extractDoc(Path filePath) throws IOException {
        if (looksLikeMimeHtml(filePath)) {
            return extractMimeHtmlDoc(filePath);
        }
        try (InputStream inputStream = Files.newInputStream(filePath);
             HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        } catch (IOException exception) {
            if (looksLikeMimeHtml(filePath)) {
                return extractMimeHtmlDoc(filePath);
            }
            throw exception;
        }
    }

    private boolean looksLikeMimeHtml(Path filePath) throws IOException {
        byte[] header = Files.readAllBytes(filePath);
        int length = Math.min(header.length, 1024);
        String start = new String(header, 0, length, StandardCharsets.UTF_8).toLowerCase();
        return start.startsWith("mime-version:")
            || start.contains("content-type: multipart/related")
            || start.contains("content-type: text/html")
            || start.contains("<html");
    }

    private String extractMimeHtmlDoc(Path filePath) throws IOException {
        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        String body = content;
        int bodyStart = content.toLowerCase().indexOf("<body");
        if (bodyStart >= 0) {
            int bodyTagEnd = content.indexOf('>', bodyStart);
            int bodyEnd = content.toLowerCase().lastIndexOf("</body>");
            if (bodyTagEnd > bodyStart) {
                body = bodyEnd > bodyTagEnd ? content.substring(bodyTagEnd + 1, bodyEnd) : content.substring(bodyTagEnd + 1);
            }
        }
        body = HTML_COMMENT.matcher(body).replaceAll(" ");
        body = STYLE_BLOCK.matcher(body).replaceAll(" ");
        body = SCRIPT_BLOCK.matcher(body).replaceAll(" ");
        body = body.replaceAll("(?i)</p>|</div>|</h[1-6]>|<br\\s*/?>|</tr>", "\n");
        body = HTML_TAG.matcher(body).replaceAll(" ");
        body = decodeHtmlEntities(body);
        body = body.replaceAll("[\\u00A0\\t\\u000B\\f\\r]+", " ");
        body = body.replaceAll("\n{2,}", "\n");
        body = body.replaceAll("[ ]{2,}", " ");
        return body.trim();
    }

    private String decodeHtmlEntities(String text) {
        return text.replace("&nbsp;", " ")
            .replace("&ensp;", " ")
            .replace("&emsp;", " ")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&ldquo;", "\"")
            .replace("&rdquo;", "\"")
            .replace("&lsquo;", "'")
            .replace("&rsquo;", "'");
    }

    private boolean hasUsableText(String text) {
        if (text == null) {
            return false;
        }
        return text.replaceAll("\\s+", "").trim().length() >= 8;
    }

    private String chooseBetterPdfText(String extracted, String sortedExtracted) {
        return textQualityScore(sortedExtracted) > textQualityScore(extracted) ? sortedExtracted : extracted;
    }

    private boolean shouldSupplementWithOcr(String text) {
        if (!hasUsableText(text)) {
            return true;
        }
        String normalized = text.replaceAll("\\s+", "");
        long lineCount = text.lines().map(String::trim).filter(line -> !line.isEmpty()).count();
        return normalized.length() < 800 || lineCount < 12;
    }

    private String chooseBestFinalText(String pdfText, String mergedText, String ocrText) {
        int pdfScore = textQualityScore(pdfText);
        int mergedScore = textQualityScore(mergedText);
        int ocrScore = textQualityScore(ocrText);
        if (mergedScore >= pdfScore && mergedScore >= ocrScore) {
            return mergedText;
        }
        return ocrScore > pdfScore ? ocrText : pdfText;
    }

    private int textQualityScore(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        String normalized = normalizeExtractedText(text);
        int hanCount = countMatches(normalized, "\\p{IsHan}");
        int alphaNumCount = countMatches(normalized, "[A-Za-z0-9]");
        int punctuationPenalty = countMatches(normalized, "[|~_^]{2,}");
        long lineCount = normalized.lines().map(String::trim).filter(line -> !line.isEmpty()).count();
        return normalized.length() + hanCount * 3 + alphaNumCount - punctuationPenalty * 6 + (int) lineCount * 5;
    }

    private int countMatches(String text, String regex) {
        return (int) Pattern.compile(regex).matcher(text).results().count();
    }

    private String mergePdfAndOcrText(String pdfText, String ocrText) {
        if (!hasUsableText(pdfText)) {
            return ocrText == null ? "" : ocrText;
        }
        if (!hasUsableText(ocrText)) {
            return pdfText;
        }
        Map<String, String> mergedLines = new LinkedHashMap<>();
        appendLines(mergedLines, pdfText);
        appendLines(mergedLines, ocrText);
        return String.join(System.lineSeparator(), mergedLines.values());
    }

    private void appendLines(Map<String, String> mergedLines, String text) {
        text.lines().map(String::trim).filter(line -> !line.isEmpty()).forEach(line -> {
            String normalizedKey = line.replaceAll("[\\s\\p{Punct}]+", "").toLowerCase();
            if (normalizedKey.isEmpty()) {
                return;
            }
            String current = mergedLines.get(normalizedKey);
            if (current == null || line.length() > current.length()) {
                mergedLines.put(normalizedKey, line);
            }
        });
    }

    private String normalizeExtractedText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\u0000", "")
            .replaceAll("[\\t\\x0B\\f\\r]+", " ")
            .replaceAll("[ ]{2,}", " ")
            .replaceAll("\\n{3,}", "\n\n")
            .trim();
    }

    private String extractDocxXmlFallback(Path filePath) throws IOException {
        try (ZipFile zipFile = new ZipFile(filePath.toFile(), StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder();
            appendDocxEntryText(zipFile, builder, "word/document.xml");
            for (int index = 1; index <= 8; index++) {
                appendDocxEntryText(zipFile, builder, "word/header" + index + ".xml");
                appendDocxEntryText(zipFile, builder, "word/footer" + index + ".xml");
                appendDocxEntryText(zipFile, builder, "word/footnotes" + index + ".xml");
                appendDocxEntryText(zipFile, builder, "word/endnotes" + index + ".xml");
            }
            appendDocxEntryText(zipFile, builder, "word/comments.xml");
            return normalizeExtractedText(builder.toString());
        }
    }

    private void appendDocxEntryText(ZipFile zipFile, StringBuilder builder, String entryName) throws IOException {
        ZipEntry entry = zipFile.getEntry(entryName);
        if (entry == null) {
            return;
        }
        try (InputStream inputStream = zipFile.getInputStream(entry)) {
            String xml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String text = convertWordXmlToText(xml);
            if (!text.isBlank()) {
                if (builder.length() > 0) {
                    builder.append(System.lineSeparator()).append(System.lineSeparator());
                }
                builder.append(text);
            }
        }
    }

    private String convertWordXmlToText(String xml) {
        String text = xml
            .replaceAll("(?i)</w:p>", "\n")
            .replaceAll("(?i)<w:tab[^>]*/>", "\t")
            .replaceAll("(?i)<w:br[^>]*/>", "\n")
            .replaceAll("(?i)<w:cr[^>]*/>", "\n")
            .replaceAll("(?i)</w:tr>", "\n")
            .replaceAll("(?i)</w:tc>", "\t")
            .replaceAll("(?i)</w:t>", "")
            .replaceAll("(?i)<w:t[^>]*>", "");
        text = HTML_COMMENT.matcher(text).replaceAll(" ");
        text = HTML_TAG.matcher(text).replaceAll(" ");
        return decodeHtmlEntities(text);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public record ExtractedText(String text, boolean ocrEnhanced, String mode) {
    }
}
