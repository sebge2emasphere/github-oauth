/**
 * i18n Tool
 * Web API of the i18n tool
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { WorkspaceReviewDto } from './workspaceReviewDto';

/**
 * Review of a workspace based on a GitHub repository. The review is thanks to a pull request.
 */
export interface WorkspaceGitHubReviewDto extends WorkspaceReviewDto { 
    /**
     * The branch used by the pull request.
     */
    pullRequestBranch: string;
    /**
     * The GitHub pull request number of this review.
     */
    pullRequestNumber: number;
}
export namespace WorkspaceGitHubReviewDto {
}
