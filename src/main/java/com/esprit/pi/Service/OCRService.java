package com.esprit.pi.Service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OCRService {

    public String extractTextFromFile(String filePath) {
        File file = new File(filePath);
        System.out.println("📂 Reading file: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.err.println("❌ File does not exist: " + filePath);
            return null;
        }

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata"); // Adjust path if needed

        try {
            return tesseract.doOCR(file);
        } catch (TesseractException e) {
            e.printStackTrace();
            return null;
        }
    }
}
