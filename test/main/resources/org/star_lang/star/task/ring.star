ring is package{
  import concurrency;

  exitChnnl is channel();

  ring has type (integer,channel of integer,channel of integer) => task of ();
  ring(Id,L,R) is task{
    logMsg(info,"spin up $Id");

    while true do {
      logMsg(info,"$Id is ready");
      case valof recv(L) in {
	1 do {
	  perform wait for put Id on exitChnnl;
	  valis ()
	}
	Tk do{
	  logMsg(info,"$Id got $Tk");
	  perform wait for put Tk-1 on R;
	  logMsg(info,"sent $(Tk-1)");
	}
      }
    }
  }

  main() do {
    HowMany is 4;
    Count is 100;
    var Lch is channel();
    var Rch is channel();

    First is background ring(1,Lch,Rch);

    var P := First;
    var C := Rch;

    for ix in range(2,HowMany,1) do {
      var Ch is channel();
      Next is background ring(ix,C,Ch);
      C := Ch;
    };

    Last is background ring(HowMany,C,Lch);

    perform wait for put Count on Lch;

    case valof recv(exitChnnl) in {
      Id do 
	logMsg(info,"Ended at $Id");
    }
  }
}
