import { Component, input } from '@angular/core';

@Component({
  selector: 'app-page-intro',
  standalone: true,
  templateUrl: './page-intro.component.html',
  styleUrl: './page-intro.component.scss',
})
/**
 * Defines the page intro component class.
 */
export class PageIntroComponent {
  kicker = input('');
  title = input.required<string>();
  description = input('');
}
