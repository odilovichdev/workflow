package org.example.projectdevtool.dto;


import lombok.Data;
import org.example.projectdevtool.entity.Users;

@Data
public class RegisterRequest{
    private String email;
    private String login;
    private String password;
    private Users.Role role;
}
