import {
  Component,
  ElementRef,
  input,
  output,
  signal,
  viewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TextBlock } from '../../models/article-block.types';
import {EditorBlockFrameComponent} from '../../../../shared/components/editor-block-frame/editor-block-frame.component';

type HeadingLevel = 1 | 2 | 3 | 4 | 5 | 6;

@Component({
  standalone: true,
  selector: 'app-text-block',
  imports: [CommonModule, FormsModule, EditorBlockFrameComponent],
  templateUrl: './text-block.component.html',
  styleUrl: './text-block.component.scss',
})
export class TextBlockComponent {
  block = input.required<TextBlock>();
  change = output<TextBlock>();
  moveUp = output<void>();
  moveDown = output<void>();
  remove = output<void>();
  isDragging = input(false);
  isDragOver = input(false);
  dragStarted = output<string>();
  dragEntered = output<string>();
  dropped = output<string>();
  dragEnded = output<void>();

  text = viewChild.required<ElementRef<HTMLTextAreaElement>>('text');

  headingLevel = signal<HeadingLevel>(2);

  onInput(v: string) {
    this.emit({ ...this.block(), content: v });
  }

  setHeadingLevel(lvl: string) {
    this.headingLevel.set(Number(lvl) as HeadingLevel);
  }

  bold() {
    this.wrapSelection('**', '**');
  }

  italic() {
    this.wrapSelection('*', '*');
  }

  quote() {
    this.prefixSelectedLines('> ');
  }

  heading() {
    const prefix = '#'.repeat(this.headingLevel()) + ' ';
    this.prefixSelectedLines(prefix, true);
  }

  bulletedList() {
    this.prefixSelectedLines('- ');
  }

  numberedList() {
    this.numberSelectedLines();
  }

  link() {
    const el = this.text().nativeElement;
    const sel = this.getSelection(el);
    const selected = sel.text || 'link text';

    const url = window.prompt('URL for the link:', 'https://');
    if (!url) return;

    const inserted = `[${selected}](${url})`;

    this.applyRangeReplacement(sel.start, sel.end, inserted, {
      start: sel.start + inserted.length,
      end: sel.start + inserted.length,
    });
  }

  private emit(v: TextBlock) {
    this.change.emit(v);
  }

  private getSelection(el: HTMLTextAreaElement) {
    const start = el.selectionStart ?? 0;
    const end = el.selectionEnd ?? 0;
    const value = el.value ?? '';
    return {
      start,
      end,
      text: value.substring(start, end),
      value,
    };
  }

  private applyRangeReplacement(
    from: number,
    to: number,
    replacement: string,
    nextSel?: { start: number; end: number }
  ) {
    const el = this.text().nativeElement;
    const value = el.value ?? '';

    const nextValue = value.slice(0, from) + replacement + value.slice(to);
    this.emit({ ...this.block(), content: nextValue });

    queueMicrotask(() => {
      const s = nextSel?.start ?? from + replacement.length;
      const e = nextSel?.end ?? s;
      el.selectionStart = s;
      el.selectionEnd = e;
      el.focus();
    });
  }

  private applyToSelectedLines(transformLines: (lines: string[]) => string[]) {
    const el = this.text().nativeElement;
    const { value, start, end } = this.getSelection(el);

    const lineStart = value.lastIndexOf('\n', start - 1) + 1;
    const lineEndIdx = value.indexOf('\n', end);
    const lineEnd = lineEndIdx === -1 ? value.length : lineEndIdx;

    const chunk = value.slice(lineStart, lineEnd);
    const replaced = transformLines(chunk.split('\n')).join('\n');

    this.applyRangeReplacement(lineStart, lineEnd, replaced, {
      start: lineStart,
      end: lineStart + replaced.length,
    });
  }

  private wrapSelection(left: string, right: string) {
    const el = this.text().nativeElement;
    const sel = this.getSelection(el);

    if (!sel.text) {
      const placeholder = 'text';
      const inserted = left + placeholder + right;

      this.applyRangeReplacement(sel.start, sel.end, inserted, {
        start: sel.start + left.length,
        end: sel.start + left.length + placeholder.length,
      });
      return;
    }

    const inserted = left + sel.text + right;

    this.applyRangeReplacement(sel.start, sel.end, inserted, {
      start: sel.start + left.length,
      end: sel.start + left.length + sel.text.length,
    });
  }

  private prefixSelectedLines(prefix: string, replaceHeading = false) {
    this.applyToSelectedLines(lines =>
      lines.map(l => {
        let line = l;

        if (replaceHeading) {
          line = line.replace(/^#{1,6}\s+/, '');
        }

        if (line.startsWith(prefix)) return line.slice(prefix.length);
        return prefix + line;
      })
    );
  }

  private numberSelectedLines() {
    this.applyToSelectedLines(lines => {
      const allNumbered = lines.every(l => /^\d+\.\s+/.test(l) || l.trim() === '');

      return lines.map((l, i) => {
        if (l.trim() === '') return l;
        if (allNumbered) return l.replace(/^\d+\.\s+/, '');
        return `${i + 1}. ${l}`;
      });
    });
  }
}
