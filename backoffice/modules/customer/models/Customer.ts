export type Customer = {
  id: string;
  email: string;
  username: string;
  firstName: string;
  lastName: string;
  createdTimestamp: Date;
};

export type CustomerCreateVM = {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  password: string;
  confirmPassword?: string;
  role: string;
};
