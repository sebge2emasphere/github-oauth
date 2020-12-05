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
import { RepositoryCreationRequestDto } from './repositoryCreationRequestDto';

/**
 * Request asking the creation of a Git repository
 */
export interface GitRepositoryCreationRequestDto extends RepositoryCreationRequestDto { 
    /**
     * Location URL of this repository
     */
    location: string;
    /**
     * Name of this repository
     */
    name: string;
    /**
     * Username to use to connect to the Git repository
     */
    username?: string;
    /**
     * Password to connect to the Git repository
     */
    password?: string;
}
export namespace GitRepositoryCreationRequestDto {
}
