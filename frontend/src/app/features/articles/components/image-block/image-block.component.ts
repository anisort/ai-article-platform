import {Component, inject, input, output, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImageBlock } from '../../models/article-block.types';
import {finalize} from 'rxjs';
import {UploadService} from '../../../../shared/services/upload.service';
import {EditorBlockFrameComponent} from '../../../../shared/components/editor-block-frame/editor-block-frame.component';

@Component({
  standalone: true,
  selector: 'app-image-block',
  imports: [CommonModule, FormsModule, EditorBlockFrameComponent],
  templateUrl: './image-block.component.html',
  styleUrl: './image-block.component.scss'
})
export class ImageBlockComponent {
  block = input.required<ImageBlock>();
  change = output<ImageBlock>();
  moveUp = output<void>();
  moveDown = output<void>();
  remove = output<void>();
  isDragging = input(false);
  isDragOver = input(false);
  dragStarted = output<string>();
  dragEntered = output<string>();
  dropped = output<string>();
  dragEnded = output<void>();

  private readonly uploadService = inject(UploadService);

  uploading = signal<boolean>(false);

  setCaption(caption: string) {
    this.emit({
      ...this.block(),
      caption,
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    this.uploading.set(true);

    this.uploadService
      .uploadFile(file)
      .pipe(finalize(() => this.uploading.set(false)))
      .subscribe({
        next: response => {
          this.emit({
            ...this.block(),
            imageUrl: response.url,
          });
        },
        error: error => {
          console.error('Upload failed', error);
        },
      });
  }

  private emit(block: ImageBlock) {
    this.change.emit(block);
  }
}
