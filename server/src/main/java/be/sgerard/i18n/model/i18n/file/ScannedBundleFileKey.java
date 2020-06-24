package be.sgerard.i18n.model.i18n.file;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translation key in a {@link ScannedBundleFile translation bundle file}.
 *
 * @author Sebastien Gerard
 */
public class ScannedBundleFileKey {

    /**
     * Merges both bundle keys together.
     */
    public static ScannedBundleFileKey merge(ScannedBundleFileKey first, ScannedBundleFileKey second) {
        if (first == null) {
            return second;
        } else {
            if (second == null) {
                return first;
            } else {
                final Map<TranslationLocaleEntity, String> translations = new LinkedHashMap<>(first.getTranslations());
                translations.putAll(second.getTranslations());

                return new ScannedBundleFileKey(first.getKey(), translations);
            }
        }
    }

    private final String key;
    private final Map<TranslationLocaleEntity, String> translations;

    public ScannedBundleFileKey(String key, Map<TranslationLocaleEntity, String> translations) {
        this.key = key;
        this.translations = translations;
    }

    /**
     * Returns the key associated to those translations.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns translations by their locales.
     */
    public Map<TranslationLocaleEntity, String> getTranslations() {
        return translations;
    }

    @Override
    public String toString() {
        return "ScannedBundleFileKey(" + key + ")";
    }
}
