import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUsersList } from '../users-list.model';
import { UsersListService } from '../service/users-list.service';

@Component({
  templateUrl: './users-list-delete-dialog.component.html',
})
export class UsersListDeleteDialogComponent {
  usersList?: IUsersList;

  constructor(protected usersListService: UsersListService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.usersListService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
