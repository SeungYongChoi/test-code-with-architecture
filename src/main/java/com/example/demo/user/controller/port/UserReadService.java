package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;

import java.util.Optional;

public interface UserReadService {
    Optional<User> findById(long id);

    User getByEmail(String email);

    User getById(long id);
}
