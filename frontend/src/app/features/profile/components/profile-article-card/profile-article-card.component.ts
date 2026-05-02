import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

export interface ProfileArticleCardData {
  id: string;
  title: string;
  imageUrl?: string;
  summary?: string;
  dateLabel: string;
  articleLink: string[];
  editLink?: string[];
}

@Component({
  selector: 'app-profile-article-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './profile-article-card.component.html',
  styleUrl: './profile-article-card.component.scss',
})
export class ProfileArticleCardComponent {
  readonly article = input.required<ProfileArticleCardData>();
}
