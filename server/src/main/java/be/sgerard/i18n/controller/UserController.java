package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.service.user.UserManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller managing users.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "User", description = "Controller of users.")
public class UserController {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Finds all users.
     */
    @GetMapping("/user")
    @Operation(summary = "Retrieves all users.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Flux<UserDto> findAll() {
        return userManager
                .findAll()
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Returns the user having the specified id.
     */
    @GetMapping(path = "/user/{id}")
    @Operation(summary = "Returns the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Mono<UserDto> findById(@PathVariable String id) {
        return userManager
                .findByIdOrDie(id)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Creates a new internal user.
     */
    @PostMapping(path = "/user")
    @Operation(summary = "Creates a new internal user.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Mono<UserDto> createUser(@RequestBody InternalUserCreationDto creationDto) {
        return userManager
                .createUser(creationDto)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Updates the user having the specified id.
     */
    @PatchMapping(path = "/user/{id}")
    @Operation(summary = "Updates the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Mono<UserDto> updateUser(@PathVariable String id,
                                    @RequestBody UserPatchDto userUpdate) {
        return userManager
                .update(id, userUpdate)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Returns the avatar of the specified user.
     */
    @Bean
    public RouterFunction<ServerResponse> getUserAvatar() {
        return RouterFunctions.route(
                RequestPredicates.path("/api/user/{id}/avatar"),
                request -> userManager
                        .findByIdOrDie(request.pathVariable("id"))
                        .flatMap(userEntity -> {
                            if (userEntity instanceof InternalUserEntity) {
                                final InternalUserEntity internalUserEntity = (InternalUserEntity) userEntity;
                                if (internalUserEntity.hasAvatar()) {
                                    final DefaultDataBuffer buffer = new DefaultDataBufferFactory().wrap(internalUserEntity.getAvatar());

                                    return ServerResponse
                                            .ok()
                                            .contentType(MediaType.IMAGE_PNG)
                                            .body(BodyInserters.fromDataBuffers(Flux.just(buffer)));
                                } else {
                                    return ServerResponse.noContent()
                                            .build();
                                }
                            } else {
                                final ExternalUserEntity externalUserEntity = (ExternalUserEntity) userEntity;

                                return ServerResponse
                                        .ok()
                                        .contentType(MediaType.IMAGE_PNG)
                                        .body(
                                                WebClient.create().get().uri(externalUserEntity.getAvatarUrl()).retrieve().bodyToFlux(byte[].class),
                                                byte[].class
                                        );
                            }
                        })
        );
    }

    /**
     * Deletes the user having the specified id.
     */
    @DeleteMapping(path = "/user/{id}")
    @Operation(summary = "Deletes the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<UserDto> deleteUserById(@PathVariable String id) {
        return userManager
                .delete(id)
                .map(entity -> UserDto.builder(entity).build());
    }
}
