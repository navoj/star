import volunteers;
import ports;

voltestreduce is connections{
  originate(p1,{notes has type occurrence of string;
                reqs has type action(string,integer);
                ques has type (string)=>integer
               });
  respond(p2,{notes has type occurrence of string;
                otherNots has type occurrence of integer;
                reqs has type action(string,integer);
                otherreqs has type action(integer,string);
                ques has type (string)=>integer;
                otherqueries has type ()=>integer;
               });
  connect(p1,p2,(volunteer notify X as X));
  connect(p1,p2,(volunteer query X as X));
}