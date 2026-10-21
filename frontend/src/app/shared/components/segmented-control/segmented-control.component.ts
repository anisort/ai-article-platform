import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

/**
 * Defines the segmented control option interface.
 */
export interface SegmentedControlOption<T extends string = string> {
  value: T;
  label: string;
  count?: number | null;
}

@Component({
  selector: 'app-segmented-control',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './segmented-control.component.html',
  styleUrl: './segmented-control.component.scss',
})
/**
 * Defines the segmented control component class.
 */
export class SegmentedControlComponent<T extends string = string> {
  options = input.required<SegmentedControlOption<T>[]>();
  active = input.required<T>();
  ariaLabel = input<string>('Segmented control');

  changed = output<T>();
}
