package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.dto.UserCreate;
import org.springframework.transaction.annotation.Transactional;

public interface UserCreateService {
    @Transactional
    User create(UserCreate userCreateDto);

}
