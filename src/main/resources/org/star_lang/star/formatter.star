/**
 * Standard formatting rules for StarRules 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
  
# ?C :: lineComment --> { commentColumn:50 };

# ?C :: blockComment --> { commentWrap:true; wrapColumn:80 };

# ?X is symbol { ?B } :: statement -->  X::{indent:0} :& B::{ breakBefore:true; breakAfter:";";indent:2; blankLines:1 };

# worksheet{?B} :: statement --> B::{breakAfter:";";indent:2;blankLines:1};

-- rules for formatting definitions
# #( ?S ; ?T )# :: statement --> T::{breakBefore:true};
# #( ?S ; )# :: statement --> { breakAfter:";"};
# ?S :: statement --> { breakAfter:";"};

-- rules for formatting type definitions
-- # type ?Tp is alias of ?T :: statement --> T::{ indent:+2 };
-- # type ?Tp is ?T :: statement --> T::{ indent:+2 };

# ?A or ?B :: valueSpecifier --> A::{breakAfter:true} :& B::{breakAfter:false};
# symbol{?Els} :: valueSpecifier -->  Els::{indent:+2};

  -- Type contracts
# contract ?Tp is { ?Body} :: statement --> Body::{indent:+2};
# implementation ?Tp is { ?Body } :: statement --> Body::{indent:+2};
  
-- Type annotations
-- # ?N has type ?T :: definition -->  T::{ indent:+2} ;
-- # ?Id has type ?Tp :: typeAnnotation --> Tp::{indent:+2};
# #( ?S ; ?T )# :: typeAnnotation --> T::{breakBefore:true};
# #( ?S ; )#::typeAnnotation --> {breakAfter:true};

-- Type Expressions
# ?Tp where ?C :: typeExpression --> C::{breakBefore:true};
# { ?S } :: typeExpression --> S::{breakBefore:true; indent:+2};

-- Function definitions
# ?Head where ?Cnd is ?Exp :: statement --> Cnd :: {indent:+4} :& Exp :: {  breakBefore:true };

-- Procedure definition
# #(?N#@#(tuple?Arg)#)#{ ?A } :: statement --> {breakAfter:true; } :& A:: {indent:+2; breakBefore:true; breakAfter:true};

-- Variable definitions
-- # ?V is ?Exp :: statement --> Exp::{indent:+2};
-- # var identifier := ?Exp :: statement --> Exp::{indent:+2};

-- Pattern abstraction definition
-- # ?name#@#(tuple?Arg)# matches ?A :: statement --> A::{indent:+10};
-- # ?name#@#(tuple?Arg)# from ?A :: statement --> A::{indent:+10};

-- Actions
# {?A} :: action --> A::{ indent:+2; breakAfter:true };

-- rules for formatting action sequences
# #( ?S ; ?T )# :: action --> T::{breakBefore:true};
# #( ?S ; )# :: action --> { breakAfter:true};

-- # ?Id is ?Exp :: action --> Exp::{indent:+2};
-- # ?N := ?Exp :: action --> Exp::{indent:+2};
-- # var identifier := ?Exp :: action --> Exp::{indent:+2};

# if ?S then ?T else ?E :: action --> T::{breakAfter:true;};
# if ?S then ?T :: action --> T::{breakAfter:true};

# for ?C do ?B :: action --> B::{breakBefore:true};
# while ?C do ?B :: action --> B::{breakBefore:true};

# let{ ?B } in ?A  :: action --> B::{indent:+2} :& A:: { breakBefore:true; indent:+4};
# ?A using { ?B } :: action --> B::{indent:+2} :& A:: { breakAfter:true; indent:+4};

# case ?E in { ?Cs } default ?D :: action --> D:: {breakBefore:true} :& Cs::{indent:+2};
# case ?E in { ?Cs } :: action --> Cs::{indent:+2};

-- request speech actions
-- # request ?Ag to ?Act :: action --> Act :: {indent:+2};
-- # request ?Act :: action --> Act :: {indent:+2};

-- default expression
-- # ?E default ?D :: expression --> D::{breakAfter:true; indent:+2};

-- Map expressions
# dictionary of {?A} :: expression --> A::{indent:+2};


# #(?L ; ?R)# :: tableEntry --> R::{breakBefore:true};

-- Query speech action
-- # query ?Ag with ?Exp :: expression --> Exp::{indent:+2};
-- # query ?Exp :: expression --> Exp::{indent:+2};

-- Relational queries
/*
# unique ?C of ?E where ?Q :: expression --> Q::{indent:+2};
# unique ?E where ?Q :: expression --> Q::{indent:+2};
# all ?E where ?Q :: expression --> Q::{indent:+2};
# anyof ?E where ?Q default ?D :: expression --> Q::{indent:+2; breakAfter:true};
# anyof ?E where ?Q :: expression  --> Q::{indent:+2};
# ?C of ?E where ?Q :: expression  --> Q::{indent:+2};
*/

-- Relation update actions
# update ?Ptn in ?Tgt with ?Exp :: action -->  Tgt::{breakAfter:true};
# extend ?Tgt with ?Exp :: action -->  Tgt::{breakAfter:true};
# merge ?Tgt with ?Exp :: action -->  Tgt::{breakAfter:true};

-- fluents
# #( ?L;?R )# :: fluentElements --> R::{breakBefore:true};

-- Actors
# actor{?B} :: expression --> B::{indent:+2}; 

# #( ?S ; ?T )#::ActorRule --> T::{breakBefore:true};

# on ?E do ?A :: ActorRule -->  A :: {breakBefore:true};

-- Action expressions
# valof{?A} ::expression --> A:: {indent:+2};
# list of {?A} :: expression --> A:: {indent:+2};

-- Record expressions
# symbol{?A} :: expression --> A::{indent:+2; breakAfter:";"};

# #( ?S ; ?T )#::attributeExpression --> T::{breakBefore:true};

-- Aggregate access expression

-- # ?L substitute ?E :: expression --> E::{indent:+2};

-- Let expression
# let {?S} in ?E :: expression --> S :: {indent:+2};
# ?E using { ?S } :: expression --> S :: {indent:+2};

-- case expression
# case ?E in { ?Cs } default ?El :: expression --> Cs::{indent:+2} :& El::{breakBefore:true};
# case ?E in { ?Cs } :: expression --> Cs::{indent:+2};

-- Lambdas

-- Conditional expression
-- # #( ?T ? ?Th : ?El)# :: expression --> Th::{indent:+2} :& El::{indent:+2};

-- conditions

-- Simple patterns

-- # ?V matching ?P :: pattern --> P::{indent:+2};

# ?P where ?C :: pattern --> C::{breakBefore:true; indent:+4};
