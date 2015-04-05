private import base;
private import strings;
private import sequences;
private import arithmetic;
private import iterable;

implementation sequence over fileStream of t determines t is {
  _cons(H,T) is __string_cons(H,T);
  _apnd(S,C) is __string_apnd(S,C); 
  _empty() from "";
  _pair(H,T) from __string_pair(H,T);
  _back(T,H) from __string_back(T,H);
  _nil() is "";
