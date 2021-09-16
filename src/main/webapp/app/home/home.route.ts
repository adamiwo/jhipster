import { Route } from '@angular/router';

import { HomeComponent } from './home.component';

export const HOME_ROUTE: Route = {
  path: '',
  redirectTo: 'login',
  pathMatch: 'full',
};
