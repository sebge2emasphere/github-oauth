package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.RepositoryManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * {@link RepositoryListener Listener} checking that repositories are valid.
 *
 * @author Sebastien Gerard
 */
@Component
public class RepositoryValidationListener implements RepositoryListener<RepositoryEntity> {

    /**
     * Validation message key specifying that the repository name is not unique.
     */
    public static final String NAME_NOT_UNIQUE = "validation.repository.name-not-unique";

    /**
     * Validation message key specifying that the repository cannot be edited.
     */
    public static final String READ_ONLY = "validation.repository.read-only";

    private final RepositoryManager repositoryManager;

    public RepositoryValidationListener(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<ValidationResult> beforePersist(RepositoryEntity repositoryEntity) {
        return repositoryManager
                .findAll()
                .filter(repo -> Objects.equals(repo.getName(), repositoryEntity.getName()))
                .collectList()
                .map(conflicts -> {
                    if (!conflicts.isEmpty()) {
                        return ValidationResult.builder()
                                .messages(new ValidationMessage(NAME_NOT_UNIQUE, repositoryEntity.getName()))
                                .build();
                    } else {
                        return ValidationResult.EMPTY;
                    }
                });
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(RepositoryEntity repository, RepositoryPatchDto patch) {
        return Mono
                .just(repository)
                .filter(repo -> (repo.getStatus() != RepositoryStatus.NOT_INITIALIZED) && (repo.getStatus() != RepositoryStatus.INITIALIZED))
                .map(rep -> ValidationResult.builder()
                        .messages(new ValidationMessage(READ_ONLY))
                        .build()
                );
    }
}
