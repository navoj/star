import volunteers;
import ports;
import person;

voltest is connections{
     originate(Ao,{DATA has type occurrence of Person; ACT has type action(Person); R has type list of ((Person,string))});
     respond(Br,{INP has type occurrence of Person});
     respond(Cr,{DATA has type occurrence of Person; ACT has type action(Person); R has type list of ((Person,string))});
     connect(Ao,Br,(volunteer X on DATA as X on INP));
     connect(Ao,Cr,(volunteer X as X));
   }
   
   /*
    Should result in:
    
    voltest is package{
      import speechContract;
      
      connectAo(Br,Cr) is let{
        pA is { DATA(X) do { Br._notify((procedure(SS) do SS.INP(X)));
                             Cr._notify((procedure(SS) do SS.DATA(X)))}
                      };
      } in port{
        _notify(Nf) do Nf(pA);
        _query(Qf,QQ,QFr) is Cr._query(Qf,QQ,QFr);
        _request(Rf,RQ,RFr) do Cr._request(Rf,RQ,RFr); 
      }
    }
    
    Which can be compiled to get the connection function connectAo
  */ 