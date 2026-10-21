import { CommonModule } from '@angular/common';
import { Component, HostListener, input, output, signal } from '@angular/core';

/**
 * Defines the select field option interface.
 */
export interface SelectFieldOption {
  value: string;
  label: string;
  disabled?: boolean;
}

@Component({
  selector: 'app-select-field',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './select-field.component.html',
  styleUrl: './select-field.component.scss',
})
/**
 * Defines the select field component class.
 */
export class SelectFieldComponent {
  readonly label = input<string>('');
  readonly value = input<string>('');
  readonly options = input<SelectFieldOption[]>([]);
  readonly disabled = input<boolean>(false);

  readonly valueChange = output<string>();
  readonly open = signal(false);

/**
 * Performs the selected label operation.
 * @returns the operation result
 */
  selectedLabel(): string {
    const items = this.options();
    const current = items.find(option => option.value === this.value());
    if (current) {
      return current.label;
    }
    return items[0]?.label ?? '';
  }

/**
 * Performs the toggle open operation.
 */
  toggleOpen(): void {
    if (this.disabled()) return;
    this.open.update(state => !state);
  }

/**
 * Performs the choose operation.
 * @param option the option
 */
  choose(option: SelectFieldOption): void {
    if (this.disabled() || option.disabled) return;
    this.valueChange.emit(option.value);
    this.open.set(false);
  }

  @HostListener('document:click')
/**
 * Performs the on document click operation.
 */
  onDocumentClick(): void {
    if (this.open()) {
      this.open.set(false);
    }
  }

/**
 * Performs the on root click operation.
 * @param event the event
 */
  onRootClick(event: MouseEvent): void {
    event.stopPropagation();
  }
}
