import { Injectable } from '@angular/core';
import { marked } from 'marked';

/**
 * Defines the markdown render service class.
 */
@Injectable({ providedIn: 'root' })
export class MarkdownRenderService {
/**
 * Performs the render operation.
 * @param text the text
 * @returns the operation result
 */
  render(text: string): string {
    const html = marked.parse(text, { breaks: true }) as string;
    return this.enhanceCodeBlocks(html);
  }

/**
 * Performs the render assistant operation.
 * @param text the text
 * @returns the operation result
 */
  renderAssistant(text: string): string {
    const html = marked.parse(text, { breaks: true }) as string;
    return this.enhanceCodeBlocks(html);
  }

/**
 * Performs the enhance code blocks operation.
 * @param html the html
 * @returns the operation result
 */
  private enhanceCodeBlocks(html: string): string {
    return html.replace(/(<pre><code[^>]*>)([\s\S]*?)(<\/code><\/pre>)/g, (full, openTag: string, codeHtml: string, closeTag: string) => {
      const langMatch = openTag.match(/language-([a-zA-Z0-9_-]+)/);
      const language = (langMatch?.[1] ?? 'text').toUpperCase();
      const codeOpenTag = openTag.replace('<pre>', '');
      return `<pre><span class="article-code-toolbar"><span class="article-code-language">${language}</span><span class="article-code-copy" role="button" tabindex="0">Copy</span></span>${codeOpenTag}${codeHtml}</code></pre>`;
    });
  }
}
