package com.smartarchive.archivemanage.service.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ArchiveTextVectorService {
    private static final int DIMENSION = 256;

    public List<Double> embed(String text) {
        double[] vector = new double[DIMENSION];
        String normalized = normalize(text);
        if (normalized.isBlank()) {
            return toList(vector);
        }
        for (int index = 0; index < normalized.length(); index++) {
            char current = normalized.charAt(index);
            int position = Math.abs(current) % DIMENSION;
            vector[position] += 1.0;
            if (index + 2 < normalized.length()) {
                int trigram = normalized.substring(index, index + 3).hashCode();
                vector[Math.abs(trigram) % DIMENSION] += 0.6;
            }
        }
        double norm = 0.0;
        for (double value : vector) {
            norm += value * value;
        }
        norm = Math.sqrt(norm);
        if (norm == 0) {
            return toList(vector);
        }
        for (int index = 0; index < vector.length; index++) {
            vector[index] = vector[index] / norm;
        }
        return toList(vector);
    }

    public int dimension() {
        return DIMENSION;
    }

    public String toPgVectorLiteral(List<Double> vector) {
        return "[" + vector.stream().map(value -> String.format(Locale.US, "%.6f", value)).collect(Collectors.joining(",")) + "]";
    }

    private List<Double> toList(double[] vector) {
        List<Double> result = new ArrayList<>(vector.length);
        for (double value : vector) {
            result.add(value);
        }
        return result;
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).replaceAll("\\s+", "").trim();
    }
}
