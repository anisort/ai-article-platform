import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-modal-layout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-layout.component.html',
  styleUrl: './modal-layout.component.scss',
})
export class ModalLayoutComponent {
  readonly title = input.required<string>();
  readonly kicker = input<string | null>(null);
  readonly searchPlaceholder = input<string | null>(null);
  readonly searchValue = input('');
  readonly contentScrollable = input(true);

  readonly searchValueChange = output<string>();

  onSearchInput(event: Event): void {
    const target = event.target as HTMLInputElement | null;
    this.searchValueChange.emit(target?.value ?? '');
  }
}
