selfSend is package{
  type nullable of %t is nil or real(%t);
  
  unreal(real(X)) is X;
  
  XX is actor{
    on X on SS do{
      logMsg(info,"We got $X");
      notify unreal(mySelf) with "$X!" on SS;
    };
    
    setMyself(A){
       mySelf := real(A);
    }
  } using {
    var mySelf := nil;
  }
  
  main() do {
    request XX to setMyself(XX);
    
    notify XX with "1" on SS;
    sleep(2000);
  }
}
 