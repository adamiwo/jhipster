jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IUsersList, UsersList } from '../users-list.model';
import { UsersListService } from '../service/users-list.service';

import { UsersListRoutingResolveService } from './users-list-routing-resolve.service';

describe('Service Tests', () => {
  describe('UsersList routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: UsersListRoutingResolveService;
    let service: UsersListService;
    let resultUsersList: IUsersList | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(UsersListRoutingResolveService);
      service = TestBed.inject(UsersListService);
      resultUsersList = undefined;
    });

    describe('resolve', () => {
      it('should return IUsersList returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUsersList = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultUsersList).toEqual({ id: 123 });
      });

      it('should return new IUsersList if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUsersList = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultUsersList).toEqual(new UsersList());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as UsersList })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUsersList = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultUsersList).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
