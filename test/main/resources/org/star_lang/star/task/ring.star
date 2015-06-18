ring is package{

  def exitChnnl is channel();

  ring has type (integer,channel of integer,channel of integer) => task of ();
  fun ring(Id,L,R) is task{
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

  prc main() do {
    def HowMany is 4;
    def Count is 100;
    def Lch is channel();
    def Rch is channel();

    def First is background ring(1,Lch,Rch);

    var P := First;
    var C := Rch;

    for ix in range(2,HowMany,1) do {
      def Ch is channel();
      def Next is background ring(ix,C,Ch);
      C := Ch;
    };

    def Last is background ring(HowMany,C,Lch);

    perform wait for put Count on Lch;

    case valof recv(exitChnnl) in {
      Id do 
	    logMsg(info,"Ended at $Id");
    }
  }
}
