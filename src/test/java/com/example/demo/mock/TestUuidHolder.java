package com.example.demo.mock;

import com.example.demo.common.service.port.UuidHolder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class TestUuidHolder implements UuidHolder {
    private final String uuid;
    public String random() {
        return this.uuid;
    }

}
