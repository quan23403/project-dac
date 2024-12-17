package com.example.ProjectDAC.request;

public record ChangePassword(String password, String repeatPassword, int otp) {
}
