package com.esprit.pi.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class QuizCertificateServiceImpl implements QuizCertificateService {

    @Override
    public ByteArrayInputStream generateCertificate(String username, String quizTitle, int score) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Create PDF document
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD);
            Paragraph title = new Paragraph("Quiz Completion Certificate", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Content
            Font contentFont = new Font(Font.HELVETICA, 16, Font.NORMAL);
            Paragraph content = new Paragraph("This is to certify that", contentFont);
            content.setAlignment(Element.ALIGN_CENTER);
            document.add(content);

            // User Name
            Font userFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph user = new Paragraph(username, userFont);
            user.setAlignment(Element.ALIGN_CENTER);
            user.setSpacingAfter(10);
            document.add(user);

            // Quiz Info
            Paragraph quizInfo = new Paragraph("has successfully completed the quiz: " + quizTitle + " with a score of " + score + ".", contentFont);
            quizInfo.setAlignment(Element.ALIGN_CENTER);
            quizInfo.setSpacingAfter(20);
            document.add(quizInfo);

            // Closing text
            Paragraph closing = new Paragraph("Congratulations on your achievement!", contentFont);
            closing.setAlignment(Element.ALIGN_CENTER);
            document.add(closing);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}