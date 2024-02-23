package com.example.demo.user.service;

import com.example.demo.mock.FackMailSender;
import com.example.demo.user.service.port.MailSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CertificationServiceTest {

    @Test
    public void 이메일과_컨텐츠가_제대로_만들어져서_보내지는지_테스트한다() {
        // given
        FackMailSender fackMailSender = new FackMailSender();
        CertificationService certificationService = new CertificationService(fackMailSender);
        String email = "test@test.com";
        long userId = 1;
        String certificationCode = "aaaa-aaa-aaa-aaaaa";

        // when
        certificationService.send(email, userId, certificationCode);

        // then
        assertThat(fackMailSender.email).isEqualTo(email);
        assertThat(fackMailSender.title).isEqualTo("Please certify your email address");
        assertThat(fackMailSender.content).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aaaa-aaa-aaa-aaaaa");
    }

}