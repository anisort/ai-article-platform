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
    path: 'articles/:id',
    loadComponent: () => import('./features/articles/pages/article-detail/article-detail.page')
      .then(p => p.ArticleDetailPage)
  },
  {
    path: 'edit-article',
    loadComponent: () => import('./features/articles/pages/edit-article/edit-article.page')
      .then(p => p.EditArticlePage)
  },
  {
    path: 'edit-article/:id',
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
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/profile/pages/profile/profile.page')
      .then(p => p.ProfilePage)
  },
  {
    path: 'profile/:id',
    loadComponent: () => import('./features/profile/pages/profile/profile.page')
      .then(p => p.ProfilePage)
  }
];
