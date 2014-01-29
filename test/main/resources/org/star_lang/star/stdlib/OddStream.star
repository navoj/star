OddStream is package {
import Prelude;
import Promise;

type OddStream of %a is OddNull
					 or OddConsC(%a, Promise of OddStream of %a);

#oddCons(?h, ?t) ==> OddConsC(?h, delay(?t));

OddCons(x, xs) from (OddConsC(x, p) where force(p) matches xs);

oddMap has type ((%a) => %b, OddStream of %a) => OddStream of %b;
oddMap(f, OddNull) is OddNull;
oddMap(f, OddCons(x, xs)) is
	oddCons(f(x), oddMap(f, xs));
	
oddFilter has type ((%a) => boolean, OddStream of %a) => OddStream of %a;
oddFilter(f, OddNull) is OddNull;
oddFilter(f, OddCons(x, xs)) is
	f(x) ? oddCons(x, oddFilter(f, xs)) | oddFilter(f, xs);

oddFilterMap has type ((%a) => Maybe of %b, OddStream of %a) => OddStream of %b;
oddFilterMap(f, OddNull) is OddNull;
oddFilterMap(f, OddCons(x, xs)) is
	case f(x) in {
		Nothing is oddFilterMap(f, xs);
		Just(y) is oddCons(y, oddFilterMap(f, xs)); 
	}; 

oddIsNull has type (OddStream of %a) => boolean;
oddIsNull(OddNull) is true;
oddIsNull(_) default is false;

oddHead has type (OddStream of %a) => %a;
oddHead(OddConsC(x, _)) is x;

oddTail has type (OddStream of %a) => OddStream of %a;
oddTail(OddCons(x, xs)) is xs;

oddTake has type (OddStream of %a, integer) => List of %a;
oddTake(_, 0) is Null;
oddTake(OddCons(x, xs), n) default is Cons(x, oddTake(xs, n-1));

oddFromList has type (List of %a) => OddStream of %a;
oddFromList(Null) is OddNull;
oddFromList(Cons(x, xs)) is oddCons(x, oddFromList(xs)); 

oddAppend has type (OddStream of %a, OddStream of %a) => OddStream of %a;
oddAppend(OddNull, o) is o;
oddAppend(OddCons(x, xs), o) is oddCons(x, oddAppend(xs, o));

}