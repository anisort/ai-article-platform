import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { IonIcon } from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import {
  chatboxEllipsesOutline,
  checkmarkOutline,
  codeSlashOutline,
  imageOutline,
  linkOutline,
  listOutline,
  reorderThreeOutline,
  textOutline
} from 'ionicons/icons';
import { SelectFieldComponent, SelectFieldOption } from '../../../../shared/components/select-field/select-field.component';

@Component({
  selector: 'app-article-editor-toolbar',
  standalone: true,
  imports: [CommonModule, IonIcon, SelectFieldComponent],
  templateUrl: './article-editor-toolbar.component.html',
  styleUrl: './article-editor-toolbar.component.scss',
})
export class ArticleEditorToolbarComponent {
  readonly headingLevel = input.required<1 | 2 | 3 | 4>();
  readonly headingDropdownOpen = input.required<boolean>();

  readonly headingDropdownToggle = output<void>();
  readonly headingSelected = output<1 | 2 | 3 | 4>();
  readonly applyHeading = output<void>();
  readonly bold = output<void>();
  readonly italic = output<void>();
  readonly underline = output<void>();
  readonly strike = output<void>();
  readonly quote = output<void>();
  readonly link = output<void>();
  readonly image = output<void>();
  readonly code = output<void>();
  readonly bulletList = output<void>();
  readonly numberedList = output<void>();
  readonly headingOptions: SelectFieldOption[] = [
    { value: '1', label: 'h1' },
    { value: '2', label: 'h2' },
    { value: '3', label: 'h3' },
    { value: '4', label: 'h4' },
    { value: '5', label: 'h5' },
    { value: '6', label: 'h6' },
  ];

  constructor() {
    addIcons({
      checkmarkOutline,
      chatboxEllipsesOutline,
      codeSlashOutline,
      imageOutline,
      linkOutline,
      listOutline,
      reorderThreeOutline,
      textOutline
    });
  }

  headingLabel(): string {
    return `h${this.headingLevel()}`;
  }

  onHeadingLevelSelect(value: string): void {
    const level = Number(value);
    if (level === 1 || level === 2 || level === 3 || level === 4) {
      this.headingSelected.emit(level);
    }
  }
}
