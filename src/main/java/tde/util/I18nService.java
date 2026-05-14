package tde.util;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18nService {
    private static final String BASE_NAME = "messages";
    private static final String MISSING_KEY_FORMAT = "??%s??";

    private final ObjectProperty<Locale> locale;
    private ResourceBundle bundle;

    public I18nService(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale must not be null");
        }

        this.locale = new SimpleObjectProperty<>(locale);
        bundle = loadBundle(locale);

        this.locale.addListener((_, _, newLocale) -> {
            if (newLocale != null) {
                bundle = loadBundle(newLocale);
            }
        });
    }

    public String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return String.format(MISSING_KEY_FORMAT, key);
        }
    }

    public String get(String key, Object... params) {
        String pattern = get(key);
        if (pattern.startsWith("??")) {
            return pattern;
        }

        try {
            return MessageFormat.format(pattern, params);
        } catch (IllegalArgumentException e) {
            return pattern;
        }
    }

    public StringBinding bind(String key) {
        return new StringBinding() {
            { bind(locale); }

            @Override
            protected String computeValue() {
                return I18nService.this.get(key);
            }
        };
    }

    public StringBinding bind(String key, Object... params) {
        return new StringBinding() {
            { bind(locale); }

            @Override
            protected String computeValue() {
                return I18nService.this.get(key, params);
            }
        };
    }


    public Locale getLocale() {
        return locale.get();
    }

    public void setLocale(Locale newLocale) {
        if (newLocale == null) {
            throw new IllegalArgumentException("Locale must not be null");
        }
        locale.set(newLocale);
    }

    private ResourceBundle loadBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle(BASE_NAME, locale);
        } catch (MissingResourceException e) {
            try {
                return ResourceBundle.getBundle(BASE_NAME, Locale.getDefault());
            } catch (MissingResourceException ex) {
                throw new IllegalStateException("No resource bundle found for base name '" + BASE_NAME + "'", ex);
            }
        }
    }
}
