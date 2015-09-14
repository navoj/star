user is package{
  type user is user{
    name has type string;
    balance has type integer;
  } implementing quotable;
}