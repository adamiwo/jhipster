import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UsersListComponent } from '../list/users-list.component';
import { UsersListDetailComponent } from '../detail/users-list-detail.component';
import { UsersListUpdateComponent } from '../update/users-list-update.component';
import { UsersListRoutingResolveService } from './users-list-routing-resolve.service';

const usersListRoute: Routes = [
  {
    path: '',
    component: UsersListComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UsersListDetailComponent,
    resolve: {
      usersList: UsersListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UsersListUpdateComponent,
    resolve: {
      usersList: UsersListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UsersListUpdateComponent,
    resolve: {
      usersList: UsersListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(usersListRoute)],
  exports: [RouterModule],
})
export class UsersListRoutingModule {}
