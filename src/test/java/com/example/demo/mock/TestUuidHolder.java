package com.example.demo.mock;

import com.example.demo.common.service.port.UuidHolder;
import lombok.Data;

import java.util.UUID;

@Data
public class TestUuidHolder implements UuidHolder {
    private String uuid;
    public String random() {
        this.uuid = UUID.randomUUID().toString();
        return this.uuid;
    }

}
