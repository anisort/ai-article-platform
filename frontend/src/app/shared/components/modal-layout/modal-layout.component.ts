import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-modal-layout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-layout.component.html',
  styleUrl: './modal-layout.component.scss',
})
/**
 * Defines the modal layout component class.
 */
export class ModalLayoutComponent {
  readonly title = input.required<string>();
  readonly kicker = input<string | null>(null);
  readonly searchPlaceholder = input<string | null>(null);
  readonly searchValue = input('');
  readonly contentScrollable = input(true);

  readonly searchValueChange = output<string>();

/**
 * Performs the on search input operation.
 * @param event the event
 */
  onSearchInput(event: Event): void {
    const target = event.target as HTMLInputElement | null;
    this.searchValueChange.emit(target?.value ?? '');
  }
}
