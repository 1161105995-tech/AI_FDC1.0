package com.smartarchive.archivemanage.service.support;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PdfOcrService {
    private final boolean enabled;
    private final Path tesseractExecutable;
    private final Path tessdataDir;
    private final int dpi;
    private final int timeoutSeconds;

    public PdfOcrService(
        @Value("${archive.ocr.enabled:true}") boolean enabled,
        @Value("${archive.ocr.tesseract-path:C:/Program Files/Tesseract-OCR/tesseract.exe}") String tesseractPath,
        @Value("${archive.ocr.tessdata-dir:./ocr/tessdata}") String tessdataDir,
        @Value("${archive.ocr.pdf-dpi:300}") int dpi,
        @Value("${archive.ocr.timeout-seconds:180}") int timeoutSeconds
    ) {
        this.enabled = enabled;
        this.tesseractExecutable = Path.of(tesseractPath).toAbsolutePath().normalize();
        this.tessdataDir = Path.of(tessdataDir).toAbsolutePath().normalize();
        this.dpi = dpi;
        this.timeoutSeconds = timeoutSeconds;
    }

    public boolean isAvailable() {
        return enabled && Files.exists(tesseractExecutable) && Files.isDirectory(tessdataDir);
    }

    public String extractPdf(Path filePath) throws IOException, InterruptedException {
        if (!isAvailable()) {
            return "";
        }
        List<String> pageTexts = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                BufferedImage image = renderer.renderImageWithDPI(pageIndex, dpi, ImageType.RGB);
                Path tempImage = Files.createTempFile("archive-pdf-ocr-", ".png");
                try {
                    ImageIO.write(image, "png", tempImage.toFile());
                    String text = runTesseract(tempImage);
                    if (!text.isBlank()) {
                        pageTexts.add(text.trim());
                    }
                } finally {
                    Files.deleteIfExists(tempImage);
                }
            }
        }
        return String.join(System.lineSeparator(), pageTexts).trim();
    }

    private String runTesseract(Path imagePath) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(tesseractExecutable.toString());
        command.add(imagePath.toString());
        command.add("stdout");
        command.add("--tessdata-dir");
        command.add(tessdataDir.toString());
        command.add("-l");
        command.add(resolveLanguages());
        command.add("--psm");
        command.add("6");

        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("OCR timeout");
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            process.getInputStream().transferTo(output);
            String text = output.toString(StandardCharsets.UTF_8);
            if (process.exitValue() != 0 && text.isBlank()) {
                throw new IllegalStateException("OCR execution failed");
            }
            return text;
        }
    }

    private String resolveLanguages() throws IOException {
        List<String> languages = Files.list(tessdataDir)
            .filter(path -> path.getFileName().toString().endsWith(".traineddata"))
            .map(path -> path.getFileName().toString().replace(".traineddata", ""))
            .sorted(Comparator.naturalOrder())
            .toList();
        List<String> ordered = new ArrayList<>();
        if (languages.contains("chi_sim")) {
            ordered.add("chi_sim");
        }
        if (languages.contains("eng")) {
            ordered.add("eng");
        }
        if (languages.contains("osd")) {
            ordered.add("osd");
        }
        if (ordered.isEmpty()) {
            throw new IllegalStateException("No OCR language pack found");
        }
        return String.join("+", ordered);
    }
}
