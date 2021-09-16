jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { UsersListService } from '../service/users-list.service';
import { IUsersList, UsersList } from '../users-list.model';

import { UsersListUpdateComponent } from './users-list-update.component';

describe('Component Tests', () => {
  describe('UsersList Management Update Component', () => {
    let comp: UsersListUpdateComponent;
    let fixture: ComponentFixture<UsersListUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let usersListService: UsersListService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UsersListUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(UsersListUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UsersListUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      usersListService = TestBed.inject(UsersListService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const usersList: IUsersList = { id: 456 };

        activatedRoute.data = of({ usersList });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(usersList));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UsersList>>();
        const usersList = { id: 123 };
        jest.spyOn(usersListService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ usersList });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: usersList }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(usersListService.update).toHaveBeenCalledWith(usersList);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UsersList>>();
        const usersList = new UsersList();
        jest.spyOn(usersListService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ usersList });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: usersList }));
        saveSubject.complete();

        // THEN
        expect(usersListService.create).toHaveBeenCalledWith(usersList);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UsersList>>();
        const usersList = { id: 123 };
        jest.spyOn(usersListService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ usersList });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(usersListService.update).toHaveBeenCalledWith(usersList);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
