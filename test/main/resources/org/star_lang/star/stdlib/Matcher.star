Matcher is package {
import Prelude;
import PPrint;

#left((docConcat), 40);

type Matcher of %a is Matcher{
  predicate has type (%a) => boolean;
  doc has type Doc;
};

valueMatches has type ((%a) => boolean, string) => Matcher of %a;
valueMatches(predicate, name) is
  Matcher{
	predicate = predicate;
	doc = docText(name);
  };

/* #### we would like to make the %as implementations of PPrintable */
valueIsEqual has type (%a) => Matcher of %a;
valueIsEqual(val) is
  Matcher{
	predicate = (function (otherVal) is val = otherVal);
	doc = docText("=") docConcat docSeparator(" to") docConcat docText(display(val));
  };

valueIs has type ((%a, %a) => boolean, string, %a) => Matcher of %a;
valueIs(eq, eqName, val) is
  Matcher{
	predicate = (function (otherVal) is
				  eq(val, otherVal));
	doc = docText(eqName) docConcat docSeparator(" to") docConcat docText(display(val));
  };

/* returns some(doc) in case of failure */
match has type (Matcher of %a, %a) => option of Doc;
match(m, v) is
  (m.predicate(v)) ? none | some(m.doc);
  
}
