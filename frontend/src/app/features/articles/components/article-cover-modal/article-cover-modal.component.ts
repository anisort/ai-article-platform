import { CommonModule } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { ModalController } from '@ionic/angular/standalone';
import { ModalLayoutComponent } from '../../../../shared/components/modal-layout/modal-layout.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';

@Component({
  selector: 'app-article-cover-modal',
  standalone: true,
  imports: [CommonModule, ModalLayoutComponent, LoadingStateComponent],
  templateUrl: './article-cover-modal.component.html',
  styleUrl: './article-cover-modal.component.scss',
})
/**
 * Defines the article cover modal component class.
 */
export class ArticleCoverModalComponent {
  private readonly modalController = inject(ModalController);

  @Input() imageUrl = '';
  @Input() uploading = false;

/**
 * Performs the on file selected operation.
 * @param event the event
 */
  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.modalController.dismiss({ action: 'upload', file });
    }
  }

/**
 * Performs the remove operation.
 */
  remove(): void {
    this.modalController.dismiss({ action: 'remove' });
  }

/**
 * Performs the close operation.
 */
  close(): void {
    this.modalController.dismiss();
  }
}
