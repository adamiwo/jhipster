import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UsersListDetailComponent } from './users-list-detail.component';

describe('Component Tests', () => {
  describe('UsersList Management Detail Component', () => {
    let comp: UsersListDetailComponent;
    let fixture: ComponentFixture<UsersListDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [UsersListDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ usersList: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(UsersListDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(UsersListDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load usersList on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.usersList).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
