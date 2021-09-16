import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUsersList, UsersList } from '../users-list.model';

import { UsersListService } from './users-list.service';

describe('Service Tests', () => {
  describe('UsersList Service', () => {
    let service: UsersListService;
    let httpMock: HttpTestingController;
    let elemDefault: IUsersList;
    let expectedResult: IUsersList | IUsersList[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(UsersListService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        firstName: 'AAAAAAA',
        lastName: 'AAAAAAA',
        email: 'AAAAAAA',
        adress: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a UsersList', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new UsersList()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a UsersList', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            firstName: 'BBBBBB',
            lastName: 'BBBBBB',
            email: 'BBBBBB',
            adress: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a UsersList', () => {
        const patchObject = Object.assign(
          {
            firstName: 'BBBBBB',
            adress: 'BBBBBB',
          },
          new UsersList()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of UsersList', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            firstName: 'BBBBBB',
            lastName: 'BBBBBB',
            email: 'BBBBBB',
            adress: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a UsersList', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addUsersListToCollectionIfMissing', () => {
        it('should add a UsersList to an empty array', () => {
          const usersList: IUsersList = { id: 123 };
          expectedResult = service.addUsersListToCollectionIfMissing([], usersList);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(usersList);
        });

        it('should not add a UsersList to an array that contains it', () => {
          const usersList: IUsersList = { id: 123 };
          const usersListCollection: IUsersList[] = [
            {
              ...usersList,
            },
            { id: 456 },
          ];
          expectedResult = service.addUsersListToCollectionIfMissing(usersListCollection, usersList);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a UsersList to an array that doesn't contain it", () => {
          const usersList: IUsersList = { id: 123 };
          const usersListCollection: IUsersList[] = [{ id: 456 }];
          expectedResult = service.addUsersListToCollectionIfMissing(usersListCollection, usersList);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(usersList);
        });

        it('should add only unique UsersList to an array', () => {
          const usersListArray: IUsersList[] = [{ id: 123 }, { id: 456 }, { id: 77772 }];
          const usersListCollection: IUsersList[] = [{ id: 123 }];
          expectedResult = service.addUsersListToCollectionIfMissing(usersListCollection, ...usersListArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const usersList: IUsersList = { id: 123 };
          const usersList2: IUsersList = { id: 456 };
          expectedResult = service.addUsersListToCollectionIfMissing([], usersList, usersList2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(usersList);
          expect(expectedResult).toContain(usersList2);
        });

        it('should accept null and undefined values', () => {
          const usersList: IUsersList = { id: 123 };
          expectedResult = service.addUsersListToCollectionIfMissing([], null, usersList, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(usersList);
        });

        it('should return initial array if no UsersList is added', () => {
          const usersListCollection: IUsersList[] = [{ id: 123 }];
          expectedResult = service.addUsersListToCollectionIfMissing(usersListCollection, undefined, null);
          expect(expectedResult).toEqual(usersListCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
