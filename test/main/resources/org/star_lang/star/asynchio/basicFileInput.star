basicFileInput is package{

  fun openFile(string(Fl)) is __asynch_open_file(Fl);
  
  prc consumeFile(Start,F) do let{
    prc consume(success((_,""))) do logMsg(info,"end of input")
     |  consume(success((Cnt,S))) do {
          logMsg(info,"next block of $Cnt bytes, #(__display(S))");
          consumeFile(Start+(Cnt as long),F);
        }
  } in { __file_read(F,Start,consume) };
     

  main has type (string)=>();
  prc main(fl) do
  {
  	def F is openFile(fl);
  	logMsg(info,__display(F));
  	
  	consumeFile(0l,F);
  	
  	-- sleep(10000l)
  }
}