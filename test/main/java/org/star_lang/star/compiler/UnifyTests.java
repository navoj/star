package org.star_lang.star.compiler;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.operators.Intrinsics;

public class UnifyTests
{
  @Test
  public void testInterfaceUnifications()
  {
    SortedMap<String, IType> mem1 = new TreeMap<String, IType>();
    mem1.put("name", StandardTypes.stringType);
    mem1.put("age", StandardTypes.integerType);

    IType t1 = TypeUtils.typeInterface(mem1);

    SortedMap<String, IType> mem2 = new TreeMap<String, IType>();
    mem2.put("name", StandardTypes.stringType);
    mem2.put("address", StandardTypes.stringType);
    IType t2 = TypeUtils.typeInterface(mem2);

    try {
      TypeUtils.unify(t1, t2, Location.nullLoc, Intrinsics.intrinsics());
      Assert.fail();
    } catch (TypeConstraintException e) {
      // Ok.
    }
  }
}
