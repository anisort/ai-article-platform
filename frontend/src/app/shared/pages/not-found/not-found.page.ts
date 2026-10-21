import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PageIntroComponent } from '../../components/page-intro/page-intro.component';

@Component({
  selector: 'app-not-found-page',
  standalone: true,
  imports: [CommonModule, RouterLink, PageIntroComponent],
  templateUrl: './not-found.page.html',
  styleUrl: './not-found.page.scss',
})
/**
 * Defines the not found page class.
 */
export class NotFoundPage {}
