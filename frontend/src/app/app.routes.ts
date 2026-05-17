import { Routes } from '@angular/router';
import { unauthGuard } from './core/guards/unauth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    loadComponent: () => import('./features/articles/pages/home/home.page')
      .then(p => p.HomePage)
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
    path: 'register',
    canActivate: [unauthGuard],
    loadComponent: () => import('./features/auth/pages/user-register/user-register.page')
      .then(p => p.UserRegisterPage)
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/profile/pages/profile/profile.page')
      .then(p => p.ProfilePage)
  },
  {
    path: 'profile/edit',
    loadComponent: () => import('./features/profile/pages/edit-profile/edit-profile.page')
      .then(p => p.EditProfilePage)
  },
  {
    path: 'profile/:id',
    loadComponent: () => import('./features/profile/pages/profile/profile.page')
      .then(p => p.ProfilePage)
  },
  {
    path: 'not-found',
    loadComponent: () => import('./shared/pages/not-found/not-found.page')
      .then(p => p.NotFoundPage)
  },
  {
    path: '**',
    redirectTo: 'not-found',
  }
];
