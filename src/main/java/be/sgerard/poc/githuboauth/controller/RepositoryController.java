package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.service.git.RepositoryAPI;
import be.sgerard.poc.githuboauth.service.git.RepositoryManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value="Controller handling GIT repository.")
public class RepositoryController {

    private final RepositoryManager repositoryManager;

    public RepositoryController(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @GetMapping("/repository/branch")
    @ApiOperation(value = "Lists all branches found on the repository.")
    public List<String> listBranches() throws Exception {
        return repositoryManager.open(RepositoryAPI::listBranches);
    }

}
