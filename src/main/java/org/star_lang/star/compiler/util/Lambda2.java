package org.star_lang.star.compiler.util;

public interface Lambda2<A, B, T>
{
  T apply(A arg,B arg2);
}
