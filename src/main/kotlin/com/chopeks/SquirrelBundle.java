package com.chopeks;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class SquirrelBundle {

    @NonNls
    public static final String BUNDLE = "com.chopeks.SquirrelBundle";
    private static ResourceBundle ourBundle;

    private SquirrelBundle() {
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
        try {
            return String.format(getBundle().getString(key), params);
        } catch (MissingResourceException e) {
            // Handle the case when the key is not found
            return key; // Or return a default message
        }
    }

    private static ResourceBundle getBundle() {
        if (ourBundle == null) {
            // Load the bundle, with fallback to default locale if the desired locale is missing
            try {
                ourBundle = ResourceBundle.getBundle(BUNDLE, Locale.getDefault());
            } catch (MissingResourceException e) {
                // If we fail to load the specific locale, fallback to the default (English)
                ourBundle = ResourceBundle.getBundle(BUNDLE, Locale.ENGLISH);
            }
        }
        return ourBundle;
    }
}
