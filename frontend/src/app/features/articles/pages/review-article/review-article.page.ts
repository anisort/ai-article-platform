// import { Component } from '@angular/core';
//
// @Component({
//   selector: 'app-review-article',
//   imports: [],
//   templateUrl: './review-article.page.html',
//   styleUrl: './review-article.page.scss'
// })
// export class ReviewArticlePage {
//
// }
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MarkdownModule } from 'ngx-markdown';

import { EditorStateService } from '../../state/editor-state.service';

@Component({
  standalone: true,
  selector: 'app-review-article-page',
  imports: [CommonModule, MarkdownModule],
  templateUrl: './review-article.page.html',
  styleUrls: ['./review-article.page.scss'],
})
export class ReviewArticlePage {

  title = '';
  markdownContent = '';

  constructor(
    private router: Router,
    private editorState: EditorStateService
  ) {
    this.title = editorState.title;
    this.markdownContent = editorState.markdownContent;

    // if (!this.blocks.length) {
    //   this.router.navigate(['/edit-article']);
    // }
  }

  backToEdit() {
    this.router.navigate(['/edit-article']);
  }

  publish() {
    console.log('Publish article', { title: this.title, markdownContent: this.markdownContent });
  }
}
