import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'users-list',
        data: { pageTitle: 'jhipsterApp.usersList.home.title' },
        loadChildren: () => import('./users-list/users-list.module').then(m => m.UsersListModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
