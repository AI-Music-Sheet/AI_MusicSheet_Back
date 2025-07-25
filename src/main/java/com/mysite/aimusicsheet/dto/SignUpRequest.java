package com.mysite.aimusicsheet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    private String userid;
    private String username;
    private String nickname;
    private String password;
    private String email;
}
