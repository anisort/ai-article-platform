import { CanDeactivateFn } from '@angular/router';

/**
 * Defines the pending changes component interface.
 */
export interface PendingChangesComponent {
  hasPendingChanges(): boolean;
}

export const pendingChangesGuard: CanDeactivateFn<PendingChangesComponent> = (component) => {
  if (!component.hasPendingChanges()) {
    return true;
  }

  return window.confirm('You have unsaved changes. Leave this page?');
};
