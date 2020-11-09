package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.security.user.ExternalUser;
import be.sgerard.i18n.model.security.user.dto.CurrentUserPasswordUpdateDto;
import be.sgerard.i18n.model.security.user.dto.CurrentUserPatchDto;
import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link UserListener user listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeUserListener implements UserListener {

    private final List<UserListener> listeners;

    @Lazy
    public CompositeUserListener(List<UserListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Mono<ValidationResult> beforePersist(InternalUserCreationDto info) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforePersist(info))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforePersist(ExternalUser info) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforePersist(info))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> afterCreate(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterCreate(user))
                .then();
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, UserPatchDto patch) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(user, patch))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(UserEntity user, CurrentUserPatchDto patch) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdate(user, patch))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdatePassword(UserEntity user, CurrentUserPasswordUpdateDto update) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdatePassword(user, update))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<ValidationResult> beforeUpdateAvatar(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeUpdateAvatar(user))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> afterUpdate(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterUpdate(user))
                .then();
    }

    @Override
    public Mono<ValidationResult> beforeDelete(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.beforeDelete(user))
                .reduce(ValidationResult::merge)
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    @Override
    public Mono<Void> afterDelete(UserEntity user) {
        return Flux
                .fromIterable(listeners)
                .flatMap(listener -> listener.afterDelete(user))
                .then();
    }
}
