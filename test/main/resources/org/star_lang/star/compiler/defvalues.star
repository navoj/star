defvalues is package{
  -- This package defines level2Function and its types and an example metaModel for a sample abstract query
 
type lvl2Fun is lvl2Fun {
  lhs has type lvl2Exp;
  rhs has type lvl2Exp;
  bindings has type dictionary of (string, lvl2Exp);
  bindings default is dictionary of [];
  rhs default is l2Integer(0);
  };
 
type lvl2Exp is l2Integer(integer)
                  or l2Float(float)
                  or l2String(string)
                  or l2Bool(boolean)
                  or l2Tuple(list of lvl2Exp)
                  or l2Agg(string, dictionary of (string, lvl2Exp))
                  or l2Map(dictionary of (lvl2Exp, lvl2Exp))
                  or l2Table(list of lvl2Exp)
                  or l2Fluent(lvl2Exp, lvl2Exp)
                  or l2Ident(string)
                  or l2Dot(lvl2Exp, string)
                  or l2Bin(lvl2Exp, lvl2BinaryOp, lvl2Exp)
                  or l2Rel(lvl2Exp, lvl2RelOp, lvl2Exp)
                  or l2Un(lvl2UnaryOp, lvl2Exp)
                  or l2All(lvl2Exp, lvl2Constraint)
                  or l2Bind(string);
                 
type lvl2BinaryOp is l2Plus or l2Minus or l2Times or l2Divide;
type lvl2RelOp is l2And or l2Or or l2Equal or l2NotEqual
                        or l2Less or l2Grtr or l2LessEq or l2GrEq;
type lvl2UnaryOp is l2Not;
type lvl2Constraint is l2Conj(lvl2Constraint, lvl2Constraint)
                              or l2In(string, lvl2Exp)
                              or l2Deflt(lvl2Constraint, lvl2Constraint);

-- This should have a default value stuck in.
def AbQuery is lvl2Fun{
      lhs = l2Ident("G");
      rhs = l2All(l2Dot(l2Ident("x"), "c1"),
                        l2In("x", l2Dot(l2Ident("G"), "table1")));
                 
};
 
  main has type action();
  prc main() do {
         logMsg(info, "$AbQuery");
  };
}
