import concurrency;

basicFileInput is package{

  openFile(string(Fl)) is __asynch_open_file(Fl);
  
  consumeFile(Start,F) do let{
    consume(success((_,""))) do logMsg(info,"end of input");
    consume(success((Cnt,S))) do {
      logMsg(info,"next block of $Cnt bytes, #(__display(S))");
      consumeFile(Start+(Cnt as long),F);
    }
  } in { __file_read(F,Start,consume) };
     

  main has type (string)=>();
  main(fl) do
  {
  	F is openFile(fl);
  	logMsg(info,__display(F));
  	
  	consumeFile(0l,F);
  	
  	-- sleep(10000l)
  }
}