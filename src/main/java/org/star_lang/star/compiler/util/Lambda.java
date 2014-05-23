package org.star_lang.star.compiler.util;

public interface Lambda<A, T>
{
  T apply(A arg);
}
