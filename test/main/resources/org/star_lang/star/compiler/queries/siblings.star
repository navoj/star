siblings is package{

  def siblings is list of [
    ("john", "peter"),
    ("john", "mary"),
    ("mary", "peter"),
    ("mary","john"),
    ("peter","john"),
    ("peter","mary")];
  
  ages has type list of ((string,integer));
  def ages is list of [
    ("john",10),
    ("peter",12),
    ("mary",8)
  ];
  
  def JS is all A where ("john",S) in siblings and (S,A) in ages order by A;
  
  def JSS is all (S,A) where ("john",S) in siblings and (S,A) in ages order by A using (<);
  
  prc main() do {
    logMsg(info,"John's siblings' ages are: $JS");
    logMsg(info,"John's siblings are: $JSS");
  }
}