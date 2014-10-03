package org.star_lang.star.data.indextree;

import java.util.Map.Entry;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/*
 * 
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
@SuppressWarnings("serial")
public class IndexSet<T> implements PrettyPrintable
{
  private final IndexTree<T, Object> tree;

  public IndexSet()
  {
    this.tree = IndexTree.emptyTree();
  }

  private IndexSet(IndexTree<T, Object> tree)
  {
    this.tree = tree;
  }

  public IndexSet<T> add(T el)
  {
    if (!tree.contains(el))
      return new IndexSet<>(tree.insrt(el, el));
    else
      return this;
  }

  public boolean isMember(T el)
  {
    return tree.contains(el);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    String sep = "";
    disp.append("{");
    for (Entry<T, Object> e : tree) {
      disp.append(sep);
      sep = ", ";
      T el = e.getKey();
      if (el instanceof PrettyPrintable)
        ((PrettyPrintable) el).prettyPrint(disp);
      else
        disp.append(el.toString());
    }
    disp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
