package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.DefaultGitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;

/**
 * {@link GitRepositoryApi Git Repository API} using a {@link GitRepositoryMock mock Git repository}.
 *
 * @author Sebastien Gerard
 */
public class GitRepositoryApiMock extends DefaultGitRepositoryApi {

    private final GitRepositoryApi.Configuration originalConfiguration;
    private final GitRepositoryMock repository;

    public GitRepositoryApiMock(GitRepositoryMock repository, Configuration originalConfiguration) {
        super(createMockedConfiguration(repository, originalConfiguration));

        this.originalConfiguration = originalConfiguration;
        this.repository = repository;
    }

    @Override
    public GitRepositoryApi validateInfo() throws RepositoryException, ValidationException {
        if (repository == null) {
            throw new ValidationException(
                    ValidationResult.builder()
                            .messages(new ValidationMessage(INVALID_URL, originalConfiguration.getRemoteUri().orElse(null)))
                            .build()
            );
        }

        if (!repository.authenticate(originalConfiguration.getUsername().orElse(null), originalConfiguration.getPassword().orElse(null))) {
            throw new ValidationException(
                    ValidationResult.builder()
                            .messages(new ValidationMessage(INVALID_CREDENTIALS, originalConfiguration.getRemoteUri().orElse(null)))
                            .build()
            );
        }

        return this;
    }

    private static Configuration createMockedConfiguration(GitRepositoryMock repository, Configuration originalConfiguration) {
        if (repository != null) {
            return new Configuration(originalConfiguration.getRepositoryLocation(), repository.getLocationUri())
                    .setDefaultBranch(originalConfiguration.getDefaultBranch())
                    .setUsername(originalConfiguration.getUsername().orElse(null))
                    .setPassword(originalConfiguration.getPassword().orElse(null))
                    .setDisplayName(originalConfiguration.getDisplayName())
                    .setEmail(originalConfiguration.getEmail());
        } else {
            return new Configuration(originalConfiguration.getRepositoryLocation());
        }
    }
}
