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
export class ArticleCoverModalComponent {
  private readonly modalController = inject(ModalController);

  @Input() imageUrl = '';
  @Input() uploading = false;

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.modalController.dismiss({ action: 'upload', file });
    }
  }

  remove(): void {
    this.modalController.dismiss({ action: 'remove' });
  }

  close(): void {
    this.modalController.dismiss();
  }
}
