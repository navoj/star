private import base;
private import strings;
private import sequences;
private import arithmetic;
private import iterable;
private import buffer;

type inputStream of t where encoding over t is 
     endStream
  or inputPoint{
    offset has type long
    buffer has type buffer of t
  }
  
type buffer of t where encoding over t is 
   inputBuffer{
     base has type long
     data has type list of t
   }