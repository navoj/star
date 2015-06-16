Promise is package {
import Prelude

type Promise of %a is alias of (() => %a)

makePromise has type (() => %a) => Promise of %a
makePromise(farg) is
	let {
		resource var hold := Left(farg) -- try to avoid space leak
	} in (function () is
			case hold in {
				Left(f) is valof {
					def val is f()
					hold := Right(val);
					valis val;
				}
				Right(val) is val
			})

force has type (Promise of %a) => %a
force(prm) is prm();

#delay(?exp) ==> makePromise((function () is ?exp))

}