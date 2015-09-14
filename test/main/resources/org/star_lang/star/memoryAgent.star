memory is package{

  memory has type actor{
    married has type relation of((string,string));
    marry has type action(string,string);
    divorce has type action(string,string);
  };
  
  memory is actor{ 
     -- (H,W) in married if (H,W) in marriages;
    } using {
    
    relation of((string,string)) var married := indexed{};
    
    marry(H,W){
      logMsg(info,"Marrying $H to $W");
      extend married with (H,W);
      logMsg(info,"Married is now $married");
    };
    
    divorce(H,W){
      logMsg(info,"$H divorcing $W");
      delete (H,W) in married;
      logMsg(info,"Married is now $married");
    };
  };
  
  
  main() do {
    request marry("J","M") to memory;
    request marry("F","K") to memory;
    
    logMsg(info,"$(query all (H,W) where (H,W) in married to memory) who is married?");
     
    request { marry("D","S"); divorce("J","M") } to memory;
    
    logMsg(info,"$(query all (H,W) where (H,W) in married to memory) who is married now?");
  }
}