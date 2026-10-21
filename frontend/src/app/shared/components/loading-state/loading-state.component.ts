import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';

@Component({
  selector: 'app-loading-state',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading-state.component.html',
  styleUrl: './loading-state.component.scss',
})
/**
 * Defines the loading state component class.
 */
export class LoadingStateComponent {
  readonly label = input('Loading');
  readonly title = input('Please wait...');
  readonly compact = input(false);
}
