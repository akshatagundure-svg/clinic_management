package com.example.springcrud.model;

public class LoginRequest {

    private String phone;
    private String password;
    private String role; // New field for Doctor, Patient, or Admin

    // Default constructor
    public LoginRequest() {
    }

    // Parameterized constructor
    public LoginRequest(String phone, String password, String role) {
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // Getter for phone
    public String getPhone() {
        return phone;
    }

    // Setter for phone
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }
}