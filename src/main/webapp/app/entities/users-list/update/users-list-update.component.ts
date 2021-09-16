import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUsersList, UsersList } from '../users-list.model';
import { UsersListService } from '../service/users-list.service';

@Component({
  selector: 'jhi-users-list-update',
  templateUrl: './users-list-update.component.html',
})
export class UsersListUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    firstName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    lastName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    adress: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
  });

  constructor(protected usersListService: UsersListService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ usersList }) => {
      this.updateForm(usersList);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const usersList = this.createFromForm();
    if (usersList.id !== undefined) {
      this.subscribeToSaveResponse(this.usersListService.update(usersList));
    } else {
      this.subscribeToSaveResponse(this.usersListService.create(usersList));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUsersList>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(usersList: IUsersList): void {
    this.editForm.patchValue({
      id: usersList.id,
      firstName: usersList.firstName,
      lastName: usersList.lastName,
      email: usersList.email,
      adress: usersList.adress,
    });
  }

  protected createFromForm(): IUsersList {
    return {
      ...new UsersList(),
      id: this.editForm.get(['id'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      adress: this.editForm.get(['adress'])!.value,
    };
  }
}
