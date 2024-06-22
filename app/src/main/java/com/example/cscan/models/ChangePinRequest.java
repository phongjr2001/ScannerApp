package com.example.cscan.models;

public class ChangePinRequest {
    private String Username ;
    private String CurrentPin ;
    private String NewPin ;

    public ChangePinRequest(String username, String currentPin, String newPin) {
        Username = username;
        CurrentPin = currentPin;
        NewPin = newPin;
    }
}
