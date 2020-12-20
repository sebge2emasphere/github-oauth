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
import { TranslationsConfigurationPatchDto } from './translationsConfigurationPatchDto';

/**
 * Request asking the creation of a repository
 */
export interface RepositoryPatchRequestDto { 
    /**
     * The id of the repository to modify
     */
    id: string;
    translationsConfiguration?: TranslationsConfigurationPatchDto;
    /**
     * Type of this repository
     */
    type: RepositoryPatchRequestDto.TypeDtoEnum;
}
export namespace RepositoryPatchRequestDto {
    export type TypeDtoEnum = 'GIT' | 'GITHUB';
    export const TypeDtoEnum = {
        GIT: 'GIT' as TypeDtoEnum,
        GITHUB: 'GITHUB' as TypeDtoEnum
    };
}