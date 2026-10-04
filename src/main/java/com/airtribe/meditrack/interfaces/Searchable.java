package com.airtribe.meditrack.interfaces;

public interface Searchable {

    boolean matches(String query);

    default boolean checkContainsIgnoreCase(String query, String search) {
        if(query == null || search == null) {
            return false;
        }
        return query.toLowerCase().contains(search.toLowerCase());
    }
}
