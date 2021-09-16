import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UsersListComponent } from './list/users-list.component';
import { UsersListDetailComponent } from './detail/users-list-detail.component';
import { UsersListUpdateComponent } from './update/users-list-update.component';
import { UsersListDeleteDialogComponent } from './delete/users-list-delete-dialog.component';
import { UsersListRoutingModule } from './route/users-list-routing.module';
import { DataTablesModule } from 'angular-datatables';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [SharedModule, UsersListRoutingModule, DataTablesModule, ReactiveFormsModule],
  declarations: [UsersListComponent, UsersListDetailComponent, UsersListUpdateComponent, UsersListDeleteDialogComponent],
  entryComponents: [UsersListDeleteDialogComponent],
})
export class UsersListModule {}
