package com.tech_nova.delivery.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private UUID id;
    private String username;
    private UserDataRole role;
    private String slackId;
}