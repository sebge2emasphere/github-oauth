package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Translation of a certain key part of translation bundle.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BundleKeyTranslation", description = "Translation of a key of a bundle file and associated to a locale.")
@JsonDeserialize(builder = BundleKeyTranslationDto.Builder.class)
public class BundleKeyTranslationDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BundleKeyTranslationEntity entity) {
        return builder()
                .id(entity.getId())
                .workspace(entity.getWorkspace())
                .locale(entity.getLocale())
                .originalValue(entity.getOriginalValue().orElse(null))
                .updatedValue(entity.getUpdatedValue().orElse(null))
                .lastEditor(entity.getLastEditor().orElse(null));
    }

    @Schema(description = "Unique identifier of a translation.", required = true)
    private final String id;

    @Schema(description = "Workspace id associated to this translation.", required = true)
    private final String workspace;

    @Schema(description = "Locale id associated to this translation.", required = true)
    private final String locale;

    @Schema(description = "The original value found when scanning bundle files (initializing step).", required = true)
    private final String originalValue;

    @Schema(description = "Value set by the end-user when editing translations.")
    private final String updatedValue;

    @Schema(description = "The username of the end-user that has edited this translation.")
    private final String lastEditor;

    private BundleKeyTranslationDto(Builder builder) {
        id = builder.id;
        workspace = builder.workspace;
        locale = builder.locale;
        originalValue = builder.originalValue;
        updatedValue = builder.updatedValue;
        lastEditor = builder.lastEditor;
    }

    /**
     * Returns the unique id of this translation.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the workspace id associated to this translation.
     */
    public String getWorkspace() {
        return workspace;
    }

    /**
     * Returns the locale id of the translation.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Returns the original translation.
     */
    public String getOriginalValue() {
        return originalValue;
    }

    /**
     * Returns the updated translation (if it was edited).
     */
    public String getUpdatedValue() {
        return updatedValue;
    }

    /**
     * Returns the id of the user that edited this translation.
     */
    public String getLastEditor() {
        return lastEditor;
    }

    /**
     * Builder of {@link BundleKeyTranslationDto bundle key translation}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String id;
        private String workspace;
        private String locale;
        private String originalValue;
        private String updatedValue;
        private String lastEditor;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder workspace(String workspace) {
            this.workspace = workspace;
            return this;
        }

        public Builder locale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder originalValue(String originalValue) {
            this.originalValue = originalValue;
            return this;
        }

        public Builder updatedValue(String updatedValue) {
            this.updatedValue = updatedValue;
            return this;
        }

        public Builder lastEditor(String lastEditor) {
            this.lastEditor = lastEditor;
            return this;
        }

        public BundleKeyTranslationDto build() {
            return new BundleKeyTranslationDto(this);
        }
    }
}
