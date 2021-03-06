bench is package {
  timer_start has type (integer, string) => (long, integer, string)
  fun timer_start(count, msg) is (nanos(), count, msg)

  timer_finish has type action((long, integer, string))
  prc timer_finish((start, count, msg)) do {
    def stop is nanos();
    def elapsed is ((stop - start) as float) / 1.0e6;
    def ops_per_sec is (count as float) / elapsed * 1.0e3;
    logMsg(info, "$count\t#msg\t$elapsed ms\t$(ops_per_sec) ops/sec");
  };

  # #(for ?i in iota(?start,?stop,?step) do ?A)# ==> {
    var i := start;
    while i=< stop do{
      A;
      i := i + step;
    }
  };
  
  prc benchNativeList(Count) do {
    var timer := timer_start(Count, "");
    var i:= nonInteger;
    idxes has type list of integer;
    def idxes is iota(0, Count-1, 1);

    logMsg(info, "******* native lists ******");
    timer := timer_start(Count, "Creating native list from iota($Count)");
    el_list has type ref list of integer;
    var el_list := iota(0,Count-1, 1);
    timer_finish(timer);

    var ignre := someValue(el_list[0]);

    timer := timer_start(Count, "Accessing all elements in native list");
    for i in el_list do {
      ignre := i;
    }
    timer_finish(timer);
    logMsg(info, "(last element: #ignre (should be: #(idxes[(Count-1)])))");

    if Count =< 100000 then {
      timer := timer_start(Count, "Changing elements in native list");
      for ix in iota(0, Count-1, 1) do {
        el_list[ix] := ix + 1;
      }
      timer_finish(timer);

      timer := timer_start(Count, "Copying native list of size #(size(el_list))");
      var tmp_list := el_list;
      timer_finish(timer);

      tmp_list[0] := 13;
      -- assert tmp_list[0]!=el_list[0];
      logMsg(info, "tmp_list[0] should be != el_list[0]: #(tmp_list[0]) != #(el_list[0])");

      timer := timer_start(Count, "Changing elements in copy of native list");
      for ix in iota(0, Count-1, 1) do {
        tmp_list[ix] := ix + 2;
      }
      timer_finish(timer);
    }

  }

  prc main () do {
    benchNativeList(10000);
  }
}
