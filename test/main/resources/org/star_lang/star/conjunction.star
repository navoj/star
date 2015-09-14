conjunction is package{

  R is relation{ {name="f"; age=23}; {name="g"; age=34}};
  
  main() do {
    QQ is all X where X.name="f" and X in R;
    -- QQ is all X where X in R and X.name="f";
    
    logMsg(info,"QQ is $QQ");
  }
}