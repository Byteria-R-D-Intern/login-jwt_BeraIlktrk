package com.staj.login_jwt.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public class UserDetailsDto {
    @NotBlank(message = "Adres boş olamaz")
    private String address;

    @Pattern(regexp = "^\\d{10}$", message = "Telefon numarası 10 haneli ve sadece rakam olmalı")
    private String phoneNumber;

    @Past(message = "Doğum tarihi geçmiş bir tarih olmalı")
    private LocalDate birthDate;

    // Getter ve Setter'lar
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
} 