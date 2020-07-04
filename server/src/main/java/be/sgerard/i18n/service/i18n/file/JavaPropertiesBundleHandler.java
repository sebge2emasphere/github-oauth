package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import be.sgerard.i18n.model.i18n.file.ScannedBundleTranslation;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.PropertyResourceBundle;

import static java.util.Collections.singleton;

/**
 * {@link BundleHandler Bundle handler} for bundle file based on Java properties.
 *
 * @author Sebastien Gerard
 */
@Component
public class JavaPropertiesBundleHandler implements BundleHandler {

    /**
     * File extension (including ".").
     */
    public static final String EXTENSION = ".properties";

    public JavaPropertiesBundleHandler() {
    }

    @Override
    public boolean support(BundleType bundleType) {
        return bundleType == BundleType.JAVA_PROPERTIES;
    }

    @Override
    public boolean continueScanning(File directory, BundleWalkContext context) {
        return context.canWalkTrough(BundleType.JAVA_PROPERTIES, directory.toPath());
    }

    @Override
    public Flux<ScannedBundleFile> scanBundles(File directory, BundleWalkContext context) {
        return context
                .getApi()
                .listNormalFiles(directory)
                .flatMap(
                        file ->
                                findLocales(file, context)
                                        .map(locale ->
                                                Mono.just(
                                                        new ScannedBundleFile(
                                                                getBundleName(file, locale),
                                                                BundleType.JAVA_PROPERTIES,
                                                                directory,
                                                                singleton(new ScannedBundleFileEntry(locale, file))
                                                        )
                                                ))
                                        .orElseGet(Mono::empty)
                )
                .groupBy(ScannedBundleFile::getName)
                .flatMap(group -> group.reduce(ScannedBundleFile::merge));
    }

    @Override
    public Flux<ScannedBundleTranslation> scanTranslations(ScannedBundleFile bundleFile, BundleWalkContext context) {
        return Flux
                .fromStream(bundleFile.getFiles().stream())
                .flatMap(file ->
                        context
                                .getApi()
                                .openInputStream(file.getFile())
                                .map(inputStream -> readTranslations(file.getFile(), inputStream))
                                .flatMapMany(translations ->
                                        Flux.fromStream(
                                                translations.keySet().stream()
                                                        .map(key -> new ScannedBundleTranslation(file, key, translations.getString(key)))
                                        )
                                )
                );
    }

    @Override
    public Mono<Void> updateBundle(ScannedBundleFile bundleFile,
                                   ScannedBundleFileEntry bundleFileEntry,
                                   Flux<ScannedBundleTranslation> translations,
                                   TranslationRepositoryWriteApi repositoryAPI) {
        return repositoryAPI
                .openAsTemp(bundleFileEntry.getFile())
                .flatMap(outputStream -> writeTranslations(translations, bundleFileEntry, outputStream));
    }

    /**
     * Finds the {@link TranslationLocaleEntity locale} from the specified file. If the file is not a bundle,
     * nothing is returned.
     */
    private Optional<TranslationLocaleEntity> findLocales(File file, BundleWalkContext context) {
        return context
                .getLocales()
                .stream()
                .filter(locale -> file.getName().toLowerCase().endsWith(getSuffix(locale)))
                .findFirst();
    }

    /**
     * Returns the bundle name based on the specified bundle file entry and the locale.
     */
    private String getBundleName(File file, TranslationLocaleEntity locale) {
        return file.getName().substring(0, file.getName().length() - getSuffix(locale).length());
    }

    /**
     * Returns the suffix of the file for the specified locale.
     */
    private String getSuffix(TranslationLocaleEntity locale) {
        return "_" + locale.toLocale().toLanguageTag().toLowerCase() + EXTENSION;
    }

    /**
     * Reads translations from the specified file using the specified stream.
     */
    private PropertyResourceBundle readTranslations(File file, InputStream fileStream) {
        try {
            return new PropertyResourceBundle(fileStream);
        } catch (IOException e) {
            throw WorkspaceException.onFileReading(file, e);
        }
    }

    /**
     * Writes the specified translations in the specified file using the output-stream.
     */
    private Mono<Void> writeTranslations(Flux<ScannedBundleTranslation> translations, ScannedBundleFileEntry file, File outputStream) {
        return Flux
                .using(
                        () -> new PropertiesConfiguration(outputStream),
                        conf -> translations.doOnNext(translation -> conf.setProperty(translation.getKey(), translation.getValue().orElse(null))),
                        conf -> {
                            try {
                                conf.save();
                            } catch (ConfigurationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .onErrorResume(e -> Mono.error(WorkspaceException.onFileWriting(file.getFile(), e)))
                .then();
    }
}
