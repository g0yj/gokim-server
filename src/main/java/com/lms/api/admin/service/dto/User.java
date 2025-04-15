package com.lms.api.admin.service.dto;

import lombok.Getter;

@Getter
public class User {
    String id;
    String password;
    String name;

    String email;
    String phone;

    String file;
    String originalFile;
}