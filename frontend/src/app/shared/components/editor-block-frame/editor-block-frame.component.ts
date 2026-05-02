import {Component, input, output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {IonIcon} from '@ionic/angular/standalone';
import {addIcons} from 'ionicons';
import {chevronDownOutline, chevronUpOutline, closeOutline, menuOutline} from 'ionicons/icons';

@Component({
  selector: 'app-editor-block-frame',
  standalone: true,
  imports: [CommonModule, IonIcon],
  templateUrl: './editor-block-frame.component.html',
  styleUrl: './editor-block-frame.component.scss'
})
export class EditorBlockFrameComponent {
  blockId = input.required<string>();
  compact = input(false);
  isDragging = input(false);
  isDragOver = input(false);
  moveUp = output<void>();
  moveDown = output<void>();
  remove = output<void>();
  dragStarted = output<string>();
  dragEntered = output<string>();
  dropped = output<string>();
  dragEnded = output<void>();

  constructor() {
    addIcons({menuOutline, chevronUpOutline, chevronDownOutline, closeOutline});
  }

  onDragStart(event: DragEvent) {
    event.dataTransfer?.setData('text/plain', this.blockId());
    event.dataTransfer!.effectAllowed = 'move';
    this.dragStarted.emit(this.blockId());
  }

  onDragEnter(event: DragEvent) {
    event.preventDefault();
    this.dragEntered.emit(this.blockId());
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'move';
    }
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.dropped.emit(this.blockId());
  }
}
