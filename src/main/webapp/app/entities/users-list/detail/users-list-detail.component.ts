import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUsersList } from '../users-list.model';

@Component({
  selector: 'jhi-users-list-detail',
  templateUrl: './users-list-detail.component.html',
})
export class UsersListDetailComponent implements OnInit {
  usersList: IUsersList | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ usersList }) => {
      this.usersList = usersList;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
