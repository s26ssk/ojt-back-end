package com.ra.config;

public class Validator {
    public static final String PHONE_REGEX = "^(84|0[3|5|7|8|9])([0-9]{8})$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    public static final String NAME_REGEX = "^.{3,50}$";
    public static final String ADDRESS_REGEX = "^.{3,200}$";
    public static final String LATITUDE_REGEX = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$";
    public static final String LONGITUDE_REGEX = "^[-+]?((1[0-7]|[0-9])?\\d(\\.\\d+)?|180(\\.0+)?)$";

    public static boolean isValidPhone(String value) {
        return value.matches(PHONE_REGEX);
    }

    public static boolean isValidEmail(String value) {
        return value.matches(EMAIL_REGEX);
    }

    public static boolean isValidName(String value) {
        return value.matches(NAME_REGEX);
    }

    public static boolean isValidAddress(String value) {
        return value.matches(ADDRESS_REGEX);
    }

    public static boolean isStrongPassword(String value) {
        return value.matches(PASSWORD_REGEX);
    }

    public static boolean isLatitude(String value) {
        return value.matches(LATITUDE_REGEX);
    }

    public static boolean isLongitude(String value) {
        return value.matches(LONGITUDE_REGEX);
    }
}
