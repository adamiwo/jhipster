export interface IUsersList {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  adress?: string | null;
}

export class UsersList implements IUsersList {
  constructor(
    public id?: number,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string | null,
    public adress?: string | null
  ) {}
}

export function getUsersListIdentifier(usersList: IUsersList): number | undefined {
  return usersList.id;
}
