package com.mtm.captaincalling;

public class ProperCase {

    public static String properCase(String inputVal) {
        // Empty strings should be returned as-is.
        if (inputVal == null || inputVal.isEmpty()) {
            return "";
        }

        // Strings with only one character uppercased.
        if (inputVal.length() == 1) {
            return inputVal.toUpperCase();
        }

        // Otherwise uppercase first letter, lowercase the rest.
        return inputVal.substring(0, 1).toUpperCase() + inputVal.substring(1).toLowerCase();
    }
}
