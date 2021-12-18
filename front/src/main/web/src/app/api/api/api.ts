export * from './authentication.service';
import { AuthenticationService } from './authentication.service';
export * from './dictionary.service';
import { DictionaryService } from './dictionary.service';
export * from './event.service';
import { EventService } from './event.service';
export * from './gitHub.service';
import { GitHubService } from './gitHub.service';
export * from './pullRequest.service';
import { PullRequestService } from './pullRequest.service';
export * from './repository.service';
import { RepositoryService } from './repository.service';
export * from './scheduledTask.service';
import { ScheduledTaskService } from './scheduledTask.service';
export * from './snapshot.service';
import { SnapshotService } from './snapshot.service';
export * from './translation.service';
import { TranslationService } from './translation.service';
export * from './translationLocale.service';
import { TranslationLocaleService } from './translationLocale.service';
export * from './user.service';
import { UserService } from './user.service';
export * from './userLiveSession.service';
import { UserLiveSessionService } from './userLiveSession.service';
export * from './userPreferences.service';
import { UserPreferencesService } from './userPreferences.service';
export * from './workspace.service';
import { WorkspaceService } from './workspace.service';
export const APIS = [
  AuthenticationService,
  DictionaryService,
  EventService,
  GitHubService,
  PullRequestService,
  RepositoryService,
  ScheduledTaskService,
  SnapshotService,
  TranslationService,
  TranslationLocaleService,
  UserService,
  UserLiveSessionService,
  UserPreferencesService,
  WorkspaceService,
];
