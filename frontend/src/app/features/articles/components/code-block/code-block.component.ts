import {Component, input, output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CodeBlock } from '../../models/article-block.types';
import {EditorBlockFrameComponent} from '../../../../shared/components/editor-block-frame/editor-block-frame.component';

@Component({
  selector: 'app-code-block',
  imports: [CommonModule, FormsModule, EditorBlockFrameComponent],
  templateUrl: './code-block.component.html',
  styleUrl: './code-block.component.scss',
  standalone: true,
})
export class CodeBlockComponent {

  block = input.required<CodeBlock>();
  change = output<CodeBlock>();
  moveUp = output<void>();
  moveDown = output<void>();
  remove = output<void>();
  isDragging = input(false);
  isDragOver = input(false);
  dragStarted = output<string>();
  dragEntered = output<string>();
  dropped = output<string>();
  dragEnded = output<void>();

  setLang(codeLanguage: string) {
    this.change.emit({ ...this.block(), codeLanguage });
  }

  setCode(code: string) {
    this.change.emit({ ...this.block(), code });
  }
}
