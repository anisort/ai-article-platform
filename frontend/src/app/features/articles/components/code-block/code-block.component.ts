import {Component, input, output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CodeBlock } from '../../models/article-block.types';

@Component({
  selector: 'app-code-block',
  imports: [CommonModule, FormsModule],
  templateUrl: './code-block.component.html',
  styleUrl: './code-block.component.scss',
  standalone: true,
})
export class CodeBlockComponent {

  block = input.required<CodeBlock>();
  change = output<CodeBlock>();

  setLang(codeLanguage: string) {
    this.change.emit({ ...this.block(), codeLanguage });
  }

  setCode(code: string) {
    this.change.emit({ ...this.block(), code });
  }
}
