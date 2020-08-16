package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.service.github.GitHubWebHookService;
import be.sgerard.i18n.service.github.external.GitHubEventType;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithJaneDoeAdminUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collection;

import static be.sgerard.test.i18n.model.GitHubBranchCreatedEventDtoTestUtils.i18nToolRelease20205BranchCreatedEvent;
import static be.sgerard.test.i18n.model.GitHubBranchDeletedEventDtoTestUtils.i18nToolRelease20204BranchDeletedEvent;
import static be.sgerard.test.i18n.model.GitHubBranchPullRequestEventDtoTestUtils.i18nToolRelease20206PullRequestEvent;
import static be.sgerard.test.i18n.model.GitHubRepositoryPatchDtoTestUtils.WEB_HOOK_SECRET;
import static be.sgerard.test.i18n.model.GitHubRepositoryPatchDtoTestUtils.i18nToolRepositoryPatchDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GitHubControllerTest extends AbstractControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setupRepo() throws Exception {
        gitRepo
                .createMockFor(i18nToolRepositoryCreationDto())
                .allowAnonymousRead()
                .onCurrentGitProject()
                .create()
                .createBranches("develop", "release/2020.6", "release/2020.4");
    }

    @AfterAll
    public void destroy() {
        gitRepo.destroyAll();
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void deletedBranchEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .hint("my-repo")
                .update(i18nToolRepositoryPatchDto())
                .initialize()
                .workspaces();

        gitRepo
                .getRepo(i18nToolRepositoryCreationDto())
                .deleteBranches("release/2020.4");

        assertThat(getWorkspacesForRepo())
                .extracting(WorkspaceDto::getBranch)
                .contains("release/2020.4");

        postRequest(i18nToolRelease20204BranchDeletedEvent(), GitHubEventType.BRANCH_DELETED, WEB_HOOK_SECRET)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(entityExchangeResult ->
                        assertThat(entityExchangeResult.getResponseBody()).isEqualTo("Signature Verified.\nReceived 155 bytes.".getBytes())
                );

        assertThat(getWorkspacesForRepo())
                .extracting(WorkspaceDto::getBranch)
                .doesNotContain("release/2020.4");
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void createdBranchEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .hint("my-repo")
                .update(i18nToolRepositoryPatchDto())
                .initialize()
                .workspaces();

        gitRepo
                .getRepo(i18nToolRepositoryCreationDto())
                .createBranches("release/2020.5");

        postRequest(i18nToolRelease20205BranchCreatedEvent(), GitHubEventType.BRANCH_CREATED, WEB_HOOK_SECRET)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(entityExchangeResult ->
                        assertThat(entityExchangeResult.getResponseBody()).isEqualTo("Signature Verified.\nReceived 155 bytes.".getBytes())
                );

        assertThat(getWorkspacesForRepo())
                .extracting(WorkspaceDto::getBranch)
                .contains("release/2020.5");
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void pullRequestEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .hint("my-repo")
                .update(i18nToolRepositoryPatchDto())
                .initialize()
                .workspaces()
                .workspaceForBranch("release/2020.6")
                .initialize()
                .publish("test");

        postRequest(i18nToolRelease20206PullRequestEvent(), GitHubEventType.PULL_REQUEST, WEB_HOOK_SECRET)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(entityExchangeResult ->
                        assertThat(entityExchangeResult.getResponseBody()).isEqualTo("Signature Verified.\nReceived 180 bytes.".getBytes())
                );

        assertThat(getWorkspacesForRepo())
                .extracting(WorkspaceDto::getStatus)
                .containsOnly(WorkspaceStatus.INITIALIZED, WorkspaceStatus.NOT_INITIALIZED, WorkspaceStatus.NOT_INITIALIZED);
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void wrongCredentials() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .update(i18nToolRepositoryPatchDto())
                .initialize();

        postRequest(i18nToolRelease20204BranchDeletedEvent(), GitHubEventType.BRANCH_DELETED, WEB_HOOK_SECRET + "wrong")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.messages[0]").isEqualTo("The signature [sha1=fb901085c0f6f1ee6dcc7aa6771f7f9d5452f96e] is invalid.");
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void noCredentialsNeeded() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .initialize();

        postRequest(i18nToolRelease20204BranchDeletedEvent(), GitHubEventType.BRANCH_DELETED, WEB_HOOK_SECRET + "wrong")
                .exchange()
                .expectStatus().isOk();
    }

    private Collection<WorkspaceDto> getWorkspacesForRepo() {
        return repository
                .forHint("my-repo")
                .initialize()
                .workspaces()
                .get();
    }

    @SuppressWarnings("SameParameterValue")
    private WebTestClient.RequestHeadersSpec<?> postRequest(Object payload, GitHubEventType eventType, String webHookSecret) throws Exception {
        final String content = objectMapper.writeValueAsString(payload);

        return webClient
                .post()
                .uri("/api/git-hub/event")
                .header(HttpHeaders.USER_AGENT, "GitHub-Hookshot/test")
                .header(GitHubWebHookService.EVENT_TYPE, eventType.getType())
                .header(GitHubWebHookService.SIGNATURE, String.format("sha1=%s", new HmacUtils(HmacAlgorithms.HMAC_SHA_1, webHookSecret).hmacHex(content)))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload);
    }
}
