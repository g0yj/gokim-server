package com.lms.api.admin.user.dto;

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