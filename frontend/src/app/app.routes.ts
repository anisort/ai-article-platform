import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'articles',
    pathMatch: 'full',
  },
  {
    path: 'articles',
    loadComponent: () => import('./features/articles/pages/article-list/article-list.page')
      .then(p => p.ArticleListPage)
  },
  {
    path: 'edit-article',
    loadComponent: () => import('./features/articles/pages/edit-article/edit-article.page')
      .then(p => p.EditArticlePage)
  },
  {
    path: 'review-article',
    loadComponent: () => import('./features/articles/pages/review-article/review-article.page')
      .then(p => p.ReviewArticlePage)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/pages/user-register/user-register.page')
      .then(p => p.UserRegisterPage)
  }
];
