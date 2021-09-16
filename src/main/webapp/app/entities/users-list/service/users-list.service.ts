import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUsersList, getUsersListIdentifier } from '../users-list.model';

export type EntityResponseType = HttpResponse<IUsersList>;
export type EntityArrayResponseType = HttpResponse<IUsersList[]>;

@Injectable({ providedIn: 'root' })
export class UsersListService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/users-lists');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(usersList: IUsersList): Observable<EntityResponseType> {
    return this.http.post<IUsersList>(this.resourceUrl, usersList, { observe: 'response' });
  }

  update(usersList: IUsersList): Observable<EntityResponseType> {
    return this.http.put<IUsersList>(`${this.resourceUrl}/${getUsersListIdentifier(usersList) as number}`, usersList, {
      observe: 'response',
    });
  }

  partialUpdate(usersList: IUsersList): Observable<EntityResponseType> {
    return this.http.patch<IUsersList>(`${this.resourceUrl}/${getUsersListIdentifier(usersList) as number}`, usersList, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUsersList>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUsersList[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUsersListToCollectionIfMissing(
    usersListCollection: IUsersList[],
    ...usersListsToCheck: (IUsersList | null | undefined)[]
  ): IUsersList[] {
    const usersLists: IUsersList[] = usersListsToCheck.filter(isPresent);
    if (usersLists.length > 0) {
      const usersListCollectionIdentifiers = usersListCollection.map(usersListItem => getUsersListIdentifier(usersListItem)!);
      const usersListsToAdd = usersLists.filter(usersListItem => {
        const usersListIdentifier = getUsersListIdentifier(usersListItem);
        if (usersListIdentifier == null || usersListCollectionIdentifiers.includes(usersListIdentifier)) {
          return false;
        }
        usersListCollectionIdentifiers.push(usersListIdentifier);
        return true;
      });
      return [...usersListsToAdd, ...usersListCollection];
    }
    return usersListCollection;
  }
}
