Routes is package {
  type OperationName is alias of string;

  fun operationNameEqual(n1, n2) is (n1 = n2);

  type Operation of %op is Operation(OperationName, %op);

  fun operationEquals(Operation(n1, o1), Operation(n2, o2)) is
    operationNameEqual(n1, n2) and (o1 = o2)

/*
 * A route specifies a sequence of production steps associated with a product.
 */

  type Route of %op is Route{
    rem has type RouteRem of %op;
  };

  routeEquals has type (Route of %op, Route of %op) => boolean where equality over %op;
  fun routeEquals(r1, r2) is r1.rem = r2.rem;

  fun routeHash(r1) is hashCode(r1.rem)

  implementation equality over (Route of %op where equality over %op) is {
    (=) = routeEquals;
    hashCode = routeHash
  };

  /* remainder of route */
  type RouteRem of %op is
     RouteList(RouteElement of %op)
     /*
      * First route is the qt zone, second one is what comes after,
      * so the outermost one is one that ends next.
      */
  or RouteQTLimit(RouteRem of %op, RouteRem of %op);

  routeRemEquals has type (RouteRem of %op, RouteRem of %op) => boolean where equality over %op;
  fun routeRemEquals(RouteList(l1), RouteList(l2)) is l1 = l2
   |  routeRemEquals(RouteQTLimit(r1in, r1after), RouteQTLimit(r2in, r2after)) is
        routeRemEquals(r2in, r2in) and routeRemEquals(r2after, r2after)
   |  routeRemEquals(_, _) default is false

   fun routeRemHash(RouteList(l1)) is hashCode("RouteList")*37+hashCode(l1)
    |  routeRemHash(RouteQTLimit(rin,rafter)) is ((hashCode("RouteQTLimit")*37)+hashCode(rin))*37+hashCode(rafter)

  implementation equality over (RouteRem of %op where equality over %op) is {
    (=) = routeRemEquals;
    hashCode = routeRemHash
  };
  
  type RouteElement of %op is
      RouteOp(Operation of %op)
    /* #### nesting does not necessarily nest durations; should enforce */
    or RouteQTZone(RouteRem of %op); /* route must not be empty */

  routeElementEquals has type ((RouteElement of %op , RouteElement of %op) => boolean) where equality over %op;
  fun routeElementEquals(RouteOp(op1), RouteOp(op2)) is operationEquals(op1, op2)
   |  routeElementEquals(RouteQTZone(r1), RouteQTZone(r2)) is
        routeRemEquals(r1, r2)
   |  routeElementEquals(_, _) default is false;

  fun routeElementHash(RouteOp(op)) is hashCode("RouteOp")*37+hashCode(op)
   |  routeElementHash(RouteQTZone(r1)) is hashCode("RouteQTZone")*37+hashCode(r1)

  implementation equality over (RouteElement of %op where equality over %op) is {
    (=) = routeElementEquals;
    hashCode = routeElementHash
  };
}
