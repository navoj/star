/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
import volunteers;
import ports;
import person;

voltest is connections{
     originate(Ao,{DATA has type stream of Person; ACT has type action(Person); R has type list of ((Person,string))});
     respond(Br,{INP has type stream of Person});
     respond(Cr,{DATA has type stream of Person; ACT has type action(Person); R has type list of ((Person,string))});
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