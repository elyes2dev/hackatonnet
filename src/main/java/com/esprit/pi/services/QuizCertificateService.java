package com.esprit.pi.services;

import java.io.ByteArrayInputStream;

public interface QuizCertificateService {
    ByteArrayInputStream generateCertificate(String username, String quizTitle, int score);

}
