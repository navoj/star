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
nanoKerberos is package {

  type status is user or administrator or guest or unknown;
  
  type allowed of %t is notAllowed or allowed(%t); 
  
  type possible of %t is impossible or possible(%t);
  
  login has type actor of{
    login has type (string,string) => allowed of integer;
    logout has type action(string);
    sessionStatus has type (integer) => status;
  }
  login is actor {
    private var users := indexed { { user="fred"; password="FRED"; status=user};
                       { user="peter"; password="PETER"; status=user};
                       { user="root" ; password="OPEN"; status=administrator} };
                       
    private var sessions := list of [];
    
    login(User,Pass) where {user=User;password=Pass} in users is valof{
      def SessionId is random(1000000);
      extend sessions with (User,SessionId);
      valis allowed(SessionId);
    };
    login(User,_) where {user=User} in users is notAllowed;
    login(U,P) where not {user=U} in users is valof{
      def SessionId is random(1000000);
      extend sessions with (U,SessionId);
      valis allowed(SessionId);
    }
    
    logout(User) do 
      delete (User,_) in sessions;
    
    sessionStatus(Id) where (User,Id) in sessions and {user=User;status=S} in users is S;
    sessionStatus(Id) where (User,Id) in sessions is guest;
  }
  
  type service of %t is alias of (actor of %a requires { validate has type (integer)=>status}) => actor of{
    provide has type (integer,string)=>allowed of possible of %t;
  };
  
  provider has type ((string)=>possible of %t)=>service of %t;
  provider(F) is actor{
    provide(SId,Key) is valof{
      def P is request validate(SId);
      if P=user or P=administrator then
        valis allowed(F(Key))
      else
        valis notAllowed
    }
  }
  
  fileProvider is let{
    files is indexed{ { name="alpha"; value="This is alpha"};
                      { name="beta"; value="This is a beta"};
                      { name="omega"; value="The last"}};
    find(Key) where {name=Key;value=V} in files is possible(V);
    find(_) default is impossible
  } in find;
  
  filer is provider(fileProvider);
  
  {
    volunteer request validate(X) from filer as request sessionStatus(X) to login
  }
  
  userAgent has type (string,string,list of string)=> actor{
    report has type ()=>string;
    fire has type action();
  } originates {
    login has type (string,string)=>allowed of integer;
    provide has type (integer,string) => allowed of possible of string;
  }
  
  userAgent(Name,Pass,Files) is actor{
    var foundFiles := list of [];
    
    fire() do {
      def Session is any of Id where (request login(Name,Pass)) matches allowed(Id);
       
      for F in Files do
      {
        if (request provide(Session,F)) matches allowed(possible(Text)) then
          foundFiles[$:] := list of [(F,Text)];
      }
    }
    
    report() is "Found files is $(all F where (F,_) in foundFiles)";
  }
  
  main() do {
    def fred is userAgent("fred","FRED",list of ["alpha","beta","omega","eta"]);
    volunteer request login(N,P) from fred as request login(N,P) to login;
    volunteer request provide(I,K) from fred as request provide(I,K) to filer;
    
    def drWho is userAgent("drWho","Who",list of ["alpha"]);
    volunteer request login(N,P) from drWho as request login(N,P) to login;
    volunteer request provide(I,K) from drWho as request provide(I,K) to filer;
    
    def root is userAgent("root","OPEN",list of ["alpha","beta","omega","eta"]);
    volunteer request login(N,P) from root as request login(N,P) to login;
    volunteer request provide(I,K) from root as request provide(I,K) to filer;
    
    request fred to fire();
    request drWho to fire();
    request root to fire();
    
    logMsg(info,"report from fred is $(query fred with report())");
    logMsg(info,"report from drWho is $(query drWho with report())");
    logMsg(info,"report from root is $(query root with report())");
  }
}
  