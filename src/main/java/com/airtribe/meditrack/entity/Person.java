package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.Validator;

public abstract class Person extends MedicalEntity {

    private static int totalPersonsCreated;

    static {
        totalPersonsCreated = 0;
        System.out.println("[MediTrack] Person subsystem initialized.");
    }

    private final String id;
    private final String name;
    private final int age;
    private final String phone;

    public Person(String id, String name, int age, String phone) throws InvalidDataException {
        Validator.validateName(name);
        Validator.validateAge(age);
        Validator.validatePhone(phone);
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        totalPersonsCreated++;
    }

    public static int getTotalPersonsCreated() {
        return totalPersonsCreated;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public abstract String describe();

    @Override
    public String displayInfo() {
        return describe();
    }

    @Override
    public String toString() {
        return describe();
    }
}
