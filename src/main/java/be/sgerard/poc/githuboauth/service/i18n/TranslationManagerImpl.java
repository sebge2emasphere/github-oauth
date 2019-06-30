package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.controller.AuthenticationController;
import be.sgerard.poc.githuboauth.model.i18n.WorkspaceStatus;
import be.sgerard.poc.githuboauth.model.i18n.dto.*;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleFileEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyEntryEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.i18n.persistence.BundleKeyEntryRepository;
import be.sgerard.poc.githuboauth.service.i18n.persistence.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class TranslationManagerImpl implements TranslationManager {

    private final WorkspaceRepository workspaceRepository;
    private final BundleKeyEntryRepository keyEntryRepository;
    private final AuthenticationController authenticationManager;

    public TranslationManagerImpl(WorkspaceRepository workspaceRepository,
                                  BundleKeyEntryRepository keyEntryRepository,
                                  AuthenticationController authenticationManager) {
        this.workspaceRepository = workspaceRepository;
        this.keyEntryRepository = keyEntryRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional(readOnly = true)
    public BundleKeysPageDto getTranslations(BundleKeysPageRequestDto searchRequest) {
        final WorkspaceEntity workspaceEntity = workspaceRepository.findById(searchRequest.getWorkspaceId())
            .orElseThrow(() -> new ResourceNotFoundException(searchRequest.getWorkspaceId()));

        final GroupedTranslations groupedEntries = doGetTranslations(searchRequest);

        return BundleKeysPageDto.builder()
            .workspaceId(workspaceEntity.getId())
            .files(
                groupedEntries.getGroups().entrySet().stream()
                    .map(file ->
                        BundleFileDto.builder()
                            .id(file.getKey().getId())
                            .name(file.getKey().getName())
                            .location(file.getKey().getLocation())
                            .keys(
                                file.getValue().entrySet().stream()
                                    .map(key ->
                                        BundleKeyDto.builder()
                                            .id(key.getKey().getId())
                                            .key(key.getKey().getKey())
                                            .entries(
                                                key.getValue().stream()
                                                    .map(keyEntry ->
                                                        BundleKeyEntryDto.builder()
                                                            .id(keyEntry.getId())
                                                            .locale(keyEntry.getLocale())
                                                            .originalValue(keyEntry.getOriginalValue().orElse(null))
                                                            .updatedValue(keyEntry.getUpdatedValue().orElse(null))
                                                            .lastEditor(keyEntry.getLastEditor().orElse(null))
                                                            .build()
                                                    )
                                                    .collect(toList())
                                            )
                                            .build()
                                    )
                                    .collect(toList())
                            )
                            .build()
                    )
                    .collect(toList())
            )
            .lastKey(groupedEntries.getLastKey().orElse(null))
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Locale> getRegisteredLocales() {
        return keyEntryRepository.findAllLocales().stream().map(Locale::forLanguageTag).collect(toList());
    }

    @Override
    @Transactional
    public void updateTranslations(String workspaceId, Map<String, String> translations) throws ResourceNotFoundException {
        final WorkspaceEntity workspace = workspaceRepository.findById(workspaceId)
            .orElseThrow(() -> new ResourceNotFoundException(workspaceId));

        if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
            throw new IllegalStateException("Cannot update translations of workspace [" + workspaceId + "], the status "
                + workspace.getStatus() + " does not allow it.");
        }

        final String username = authenticationManager.getCurrentUser().getUsername();

        for (Map.Entry<String, String> updateEntry : translations.entrySet()) {
            final BundleKeyEntryEntity entry = keyEntryRepository.findById(updateEntry.getKey())
                .orElseThrow(() -> new ResourceNotFoundException(workspaceId));

            if (!Objects.equals(workspace.getId(), entry.getBundleKey().getBundleFile().getWorkspace().getId())) {
                throw new IllegalArgumentException("The entry [" + entry.getId() + "] does not belong to the workspace [" + workspace.getId() + "].");
            }

            entry.setLastEditor(username);
            entry.setUpdatedValue(mapToNullIfEmpty(updateEntry.getValue()));
        }
    }

    private GroupedTranslations doGetTranslations(BundleKeysPageRequestDto searchRequest) {
        final GroupedTranslations groupedTranslations = new GroupedTranslations();

        keyEntryRepository
            .searchEntries(
                BundleKeyEntrySearchRequestDto.builder(searchRequest.getWorkspaceId())
                    .missingLocales(searchRequest.getMissingLocales())
                    .locales(searchRequest.getLocales())
                    .keyPattern(searchRequest.getKeyPattern().orElse(null))
                    .hasBeenUpdated(searchRequest.hasBeenUpdated().orElse(null))
                    .lastKey(searchRequest.getLastKey().orElse(null))
                    .maxKeyEntries(searchRequest.getMaxKeys() * getRegisteredLocales().size())
                    .build()
            )
            .filter(entryEntity -> groupedTranslations.getNumberEntries() <= searchRequest.getMaxKeys())
            .forEach(entryEntity -> {
                final BundleKeyEntity bundleKey = entryEntity.getBundleKey();
                final BundleFileEntity bundleFile = bundleKey.getBundleFile();

                groupedTranslations.getGroups().putIfAbsent(bundleFile, new LinkedHashMap<>());

                if (!groupedTranslations.getGroups().get(bundleFile).containsKey(bundleKey)) {
                    groupedTranslations.incrementNumberEntries();
                    groupedTranslations.setLastKey(bundleKey.getId());

                    groupedTranslations.getGroups().get(bundleFile).putIfAbsent(bundleKey, new ArrayList<>());
                }

                groupedTranslations.getGroups().get(bundleFile).get(bundleKey).add(entryEntity);
            });

        return groupedTranslations;
    }

    private static final class GroupedTranslations {

        private final Map<BundleFileEntity, Map<BundleKeyEntity, List<BundleKeyEntryEntity>>> groups = new LinkedHashMap<>();
        private String lastKey;
        private int numberEntries;

        public GroupedTranslations() {
        }

        public Map<BundleFileEntity, Map<BundleKeyEntity, List<BundleKeyEntryEntity>>> getGroups() {
            return groups;
        }

        public Optional<String> getLastKey() {
            return Optional.ofNullable(lastKey);
        }

        public void setLastKey(String lastKey) {
            this.lastKey = lastKey;
        }

        public int getNumberEntries() {
            return numberEntries;
        }

        public void incrementNumberEntries() {
            numberEntries++;
        }
    }
}
