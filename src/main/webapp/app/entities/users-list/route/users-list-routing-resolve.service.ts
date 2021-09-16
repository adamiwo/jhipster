import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUsersList, UsersList } from '../users-list.model';
import { UsersListService } from '../service/users-list.service';

@Injectable({ providedIn: 'root' })
export class UsersListRoutingResolveService implements Resolve<IUsersList> {
  constructor(protected service: UsersListService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUsersList> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((usersList: HttpResponse<UsersList>) => {
          if (usersList.body) {
            return of(usersList.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UsersList());
  }
}
