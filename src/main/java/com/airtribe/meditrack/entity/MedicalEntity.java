package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

public abstract class MedicalEntity {

    private static LocalDateTime createdAt;

    protected MedicalEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public abstract String displayInfo();
}
