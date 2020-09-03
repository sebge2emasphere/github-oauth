import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationLocaleSelectorComponent} from "./component/translation-locale-selector/translation-locale-selector.component";
import {CoreSharedModule} from "../shared/core-shared-module";
import {CoreSharedLibModule} from "../shared/core-shared-lib.module";
import {WorkspaceSelectorComponent} from "./component/workspace-selector/workspace-selector.component";
import {WorkspacesStartReviewDialogComponent} from "./component/workspaces-start-review-dialog/workspaces-start-review-dialog.component";
import {FormWorkspacesStartReviewButtonComponent} from "./component/form-publish-button/form-workspaces-start-review-button.component";

@NgModule({
    imports: [
        CommonModule,
        CoreSharedLibModule,
        CoreSharedModule,
    ],
    declarations: [
        TranslationLocaleSelectorComponent,
        WorkspaceSelectorComponent,
        FormWorkspacesStartReviewButtonComponent,
    ],
    exports: [
        TranslationLocaleSelectorComponent,
        WorkspaceSelectorComponent,
        FormWorkspacesStartReviewButtonComponent,
    ],
    entryComponents: [
        WorkspacesStartReviewDialogComponent
    ]
})
export class CoreTranslationModule {

}
