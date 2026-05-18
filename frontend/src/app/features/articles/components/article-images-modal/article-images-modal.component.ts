import { CommonModule } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { ModalController } from '@ionic/angular/standalone';
import { ModalLayoutComponent } from '../../../../shared/components/modal-layout/modal-layout.component';
import { LoadingStateComponent } from '../../../../shared/components/loading-state/loading-state.component';
import { ArticleImage } from '../../models/article-image.model';

@Component({
  selector: 'app-article-images-modal',
  standalone: true,
  imports: [CommonModule, ModalLayoutComponent, LoadingStateComponent],
  templateUrl: './article-images-modal.component.html',
  styleUrl: './article-images-modal.component.scss',
})
export class ArticleImagesModalComponent {
  private readonly modalController = inject(ModalController);

  @Input() articleId: string | null = null;
  @Input() uploading = false;
  @Input() images: ArticleImage[] = [];

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.modalController.dismiss({ action: 'upload', file });
    }
  }

  insert(image: ArticleImage): void {
    this.modalController.dismiss({ action: 'insert', image });
  }

  remove(image: ArticleImage): void {
    this.modalController.dismiss({ action: 'remove', image });
  }

  close(): void {
    this.modalController.dismiss();
  }
}
