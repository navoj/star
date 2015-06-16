package org.star_lang.star.compiler.util;

import java.util.Iterator;

public class ComboIterable<T> implements Iterable<T>
{
  private final Iterable<T> left, right;

  public ComboIterable(Iterable<T> left, Iterable<T> right)
  {
    this.left = left;
    this.right = right;
  }

  @Override
  public Iterator<T> iterator()
  {
    return new ComboIterator<>(left.iterator(), right.iterator());
  }

}
