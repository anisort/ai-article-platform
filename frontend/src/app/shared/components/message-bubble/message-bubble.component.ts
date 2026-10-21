import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-message-bubble',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './message-bubble.component.html',
  styleUrl: './message-bubble.component.scss',
})
/**
 * Defines the message bubble component class.
 */
export class MessageBubbleComponent {
  readonly html = input.required<string>();
  readonly isUser = input<boolean>(false);
  readonly showInsert = input<boolean>(false);
  readonly insertLabel = input<string>('Insert');

  readonly insert = output<void>();
}
