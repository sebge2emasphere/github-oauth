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
 * Translation of a key of a bundle file and associated to a locale.
 */
export interface TranslationDto {
  /**
   * Unique identifier of a translation.
   */
  id: string;
  /**
   * Workspace id associated to this translation.
   */
  workspace: string;
  /**
   * Bundle id associated to this translation.
   */
  bundleFile: string;
  /**
   * Bundle key associated to this translation.
   */
  bundleKey: string;
  /**
   * Locale id associated to this translation.
   */
  locale: string;
  /**
   * The original value found when scanning bundle files (initializing step).
   */
  originalValue: string;
  /**
   * Value set by the end-user when editing translations.
   */
  updatedValue?: string;
  /**
   * The username of the end-user that has edited this translation.
   */
  lastEditor?: string;
}
