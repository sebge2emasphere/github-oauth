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

/**
 * Roles that has an authenticated user on a repository.
 */
export interface RepositoryRolesDto { 
    /**
     * Unique id of the associated repository.
     */
    repository?: string;
    /**
     * All the roles that the user has on this repository.
     */
    sessionRoles?: Array<RepositoryRolesDto.SessionRolesDtoEnum>;
}
export namespace RepositoryRolesDto {
    export type SessionRolesDtoEnum = 'MEMBER_OF_ORGANIZATION' | 'MEMBER_OF_REPOSITORY' | 'ADMIN';
    export const SessionRolesDtoEnum = {
        MEMBEROFORGANIZATION: 'MEMBER_OF_ORGANIZATION' as SessionRolesDtoEnum,
        MEMBEROFREPOSITORY: 'MEMBER_OF_REPOSITORY' as SessionRolesDtoEnum,
        ADMIN: 'ADMIN' as SessionRolesDtoEnum
    };
}