-- Wadler's pretty-printer, strict version from Lindig's paper
PPrint is package {
private import Prelude;

type Doc is DocNil
		 or DocCons(Doc, Doc)
		 or DocText(string)
		 or DocNest(integer, Doc)
		 or DocBreak(string)
		 or DocGroup(Doc);

#left((docConcat), 40);

(docConcat) has type (Doc, Doc) => Doc;
DocNil docConcat r is r;
l docConcat DocNil is l;
l docConcat r default is DocCons(l, r);

docEmpty has type Doc;
docEmpty is DocNil;

docText has type (string) => Doc;
docText(s) is DocText(s);

docNest has type (integer, Doc) => Doc;
docNest(i, DocNil) is DocNil;
docNest(i, d) default is DocNest(i, d);

docBreak has type Doc;
docBreak is DocBreak(" ");

-- either a line break or the specific string
docBreakWith has type (string) => Doc;
docBreakWith(s) is DocBreak(s);

docGroup has type (Doc) => Doc;
docGroup(d) is DocGroup(d);

-- concatenate two documents with an optional line break in between
#left((docConcatBreak), 40);

(docConcatBreak) has type (Doc, Doc) => Doc;
DocNil docConcatBreak r is r;
l docConcatBreak DocNil is l;
DocNil docConcatBreak r is r;
l docConcatBreak r default is l docConcat docBreak docConcat r;

-- concatenate two documents with either a separator or line break between them
docConcatBreakWith has type (Doc, string, Doc) => Doc;
docConcatBreakWith(DocNil, _, r) is r;
docConcatBreakWith(l, _, DocNil) is l;
docConcatBreakWith(l, b, r) is l docConcat docBreakWith(b) docConcat(r);

-- separator, optionally followed by line break
docSeparator has type (string) => Doc;
docSeparator(s) is docText(s) docConcat docBreak;

-- concatenate two documents with a separator, followed by a break
docConcatSeparate has type (Doc, string, Doc) => Doc;
docConcatSeparate(DocNil, _, r) is r;
docConcatSeparate(l, _, DocNil) is l;
docConcatSeparate(l, s, r) default is
	l docConcat docSeparator(s) docConcat r;

type SDoc is SNil
		  or SText(string, SDoc)
		  or SLine(integer, SDoc);
		  
sDocToString has type (SDoc) => string;
sDocToString(SNil) is "";
sDocToString(SText(s, d)) is s ++ sDocToString(d);
sDocToString(SLine(i, d)) is
	"\n" ++ makeSpaces(i) ++ sDocToString(d);

type DocMode is Flat or Break;

docFits has type (integer, List of ((integer, DocMode, Doc))) => boolean;
docFits(w, lis) is
	(w < 0) 
	? false
	| (case lis in {
		Null is true;
		Cons((i, m, DocNil), z) is docFits(w, z);
		Cons((i, m, DocCons(x, y)), z) is
			docFits(w, Cons((i, m, x), Cons((i, m, y), z)));
		Cons((i, m, DocNest(j, x)), z) is
			docFits(w, Cons((i+j, m, x), z));
		Cons((i, m, DocText(s)), z) is
			docFits(w - size(s), z);
		Cons((i, Flat, DocBreak(s)), z) is
			docFits(w - size(s), z);
		Cons((i, Break, DocBreak(_)), z) is true; -- impossible
		Cons((i, m, DocGroup(x)), z) is
			docFits(w, Cons((i, Flat, x), z));
	});

docFormat has type (integer, integer, List of ((integer, DocMode, Doc))) => SDoc;
docFormat(w, k, lis) is
	case lis in {
		Null is SNil;
		Cons((i, m, DocNil), z) is docFormat(w, k, z);
		Cons((i, m, DocCons(x, y)), z) is
			docFormat(w, k, Cons((i, m, x), Cons((i, m, y), z)));
		Cons((i, m, DocNest(j, x)), z) is
			docFormat(w, k, Cons((i+j, m, x), z));
		Cons((i, m, DocText(s)), z) is
			SText(s, docFormat(w, k + size(s), z));
		Cons((i, Flat, DocBreak(s)), z) is
			SText(s, docFormat(w, k + size(s), z));
		Cons((i, Break, DocBreak(s)), z) is
			SLine(i, docFormat(w, i, z));
		Cons((i, m, DocGroup(x)), z) is
			docFits(w-k, Cons((i, Flat, x), z))
			? docFormat(w, k, Cons((i, Flat, x), z))
			| docFormat(w, k, Cons((i, Break, x), z))
	};	 

-- pretty-print a document to a given width
docPretty has type (integer, Doc) => string;
docPretty(w, d) is
	let {
		sDoc is docFormat(w, 0, List1((0, Flat, DocGroup(d))));
		str is sDocToString(sDoc);
	} in str;

-- concatenate two documents with spaces in between
#left((docConcatSpace), 40);
(docConcatSpace) has type (Doc, Doc) => Doc;
DocNil docConcatSpace r is r;
l docConcatSpace DocNil is l;
l docConcatSpace r default is l docConcat docText(" ") docConcat r;

-- combine a list of documents
docFold has type ((Doc, Doc) => Doc, list of Doc) => Doc;
docFold(f, list of []) is DocNil;
docFold(f, lis) default is valof {
	var r := DocNil;
	for d[_] down lis do {
		r := f(d, r);
	}
	valis r;
};

-- concatenate a list of documents with a space in between
docSpread has type (list of Doc) => Doc;
docSpread(lis) is docFold((docConcatSpace), lis);

-- stack a list of documents
docStack has type (list of Doc) => Doc;
docStack(lis) is docFold((docConcatBreak), lis);

-- stack a list of documents with a given replacement separator
docStackWith has type (string, list of Doc) => Doc;
docStackWith(s, lis) is
	docFold((function (l, r) is docConcatBreakWith(l, s, r)), lis);

-- bracket a document with "parantheses" left and right; indent by 2
docBracket has type (string, Doc, string) => Doc;
docBracket(l, x, r) is
	docGroup(docText(l)
	         docConcat docNest(2, docBreak docConcat x)
	         docConcat docBreak
	         docConcat docText(r));

docSpreadWith has type (string, list of Doc) => Doc;
docSpreadWith(s, lis) is
	docFold((function (l, r) is docConcatSeparate(l, s, r)), lis);

-- helper
makeSpaces has type (integer) => string;
-- pretend we care about efficiency
makeSpaces(0) is "";
makeSpaces(n) where n%2 = 0 is
	let {
		half is makeSpaces(n/2);
	} in half ++ half;
makeSpaces(n) default is
	let {
		half is makeSpaces(n/2);
	} in half ++ half ++ " ";

contract PPrintable of %a is {
  show has type (%a) => Doc;
};

}
