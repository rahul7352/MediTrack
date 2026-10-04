package com.airtribe.meditrack.util;

import com.airtribe.meditrack.enums.Specialization;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AIHelper {
    private static final Map<String, Specialization> SYMPTOM_RULES = new LinkedHashMap<>();

    static {
        SYMPTOM_RULES.put("chest pain", Specialization.CARDIOLOGY);
        SYMPTOM_RULES.put("heart", Specialization.CARDIOLOGY);
        SYMPTOM_RULES.put("skin", Specialization.DERMATOLOGY);
        SYMPTOM_RULES.put("rash", Specialization.DERMATOLOGY);
        SYMPTOM_RULES.put("headache", Specialization.NEUROLOGY);
        SYMPTOM_RULES.put("migraine", Specialization.NEUROLOGY);
        SYMPTOM_RULES.put("joint pain", Specialization.ORTHOPEDICS);
        SYMPTOM_RULES.put("fracture", Specialization.ORTHOPEDICS);
        SYMPTOM_RULES.put("child", Specialization.PEDIATRICS);
        SYMPTOM_RULES.put("infant", Specialization.PEDIATRICS);
        SYMPTOM_RULES.put("ear", Specialization.ENT);
        SYMPTOM_RULES.put("throat", Specialization.ENT);
        SYMPTOM_RULES.put("anxiety", Specialization.PSYCHIATRY);
        SYMPTOM_RULES.put("stress", Specialization.PSYCHIATRY);
    }

    private AIHelper() {}

    public static Specialization getRecommendedSpecialization(String symptoms) {
        if(symptoms == null || symptoms.trim().isEmpty()) {
            return Specialization.GENERAL_MEDICINE;
        }
        for(Map.Entry<String, Specialization> map : SYMPTOM_RULES.entrySet()) {
            if(symptoms.toLowerCase().contains(map.getKey())) {
                return map.getValue();
            }
        }
        return Specialization.GENERAL_MEDICINE;
    }
}
