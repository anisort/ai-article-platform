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
import {CodeBlock, EditorBlock, ImageBlock, TextBlock} from '../../models/article-block.types';

@Component({
  standalone: true,
  selector: 'app-review-article-page',
  imports: [CommonModule, MarkdownModule],
  templateUrl: './review-article.page.html',
  styleUrls: ['./review-article.page.scss'],
})
export class ReviewArticlePage {

  title = '';
  blocks: EditorBlock[] = [];

  constructor(
    private router: Router,
    private editorState: EditorStateService
  ) {
    this.title = editorState.title;
    this.blocks = editorState.blocks;

    console.log('REVIEW state blocks:', editorState.blocks);

    // if (!this.blocks.length) {
    //   this.router.navigate(['/edit-article']);
    // }
  }

  backToEdit() {
    this.router.navigate(['/edit-article']);
  }

  publish() {
    console.log('Publish article', { title: this.title, blocks: this.blocks });
  }

  isTextBlock(b: EditorBlock): b is TextBlock {
    return b.type === 'TEXT';
  }

  isImageBlock(b: EditorBlock): b is ImageBlock {
    return b.type === 'IMAGE';
  }

  isCodeBlock(b: EditorBlock): b is CodeBlock {
    return b.type === 'CODE';
  }
}
