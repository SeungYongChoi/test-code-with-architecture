package com.example.demo.user.controller.port;

public interface CertificationService {
    void send(String email, long userId, String certificationCode);

    String generateCertificationUrl(long userId, String certificationCode);
}
