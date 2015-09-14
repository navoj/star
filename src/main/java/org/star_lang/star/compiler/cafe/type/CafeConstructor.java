package org.star_lang.star.compiler.cafe.type;

import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class CafeConstructor extends ConstructorSpecifier
{
  public CafeConstructor(Location loc, String label, int conIx, IType conType, Class<?> cafeClass, Class<?> cafeOwner)
  {
    super(loc, null, label, conIx, conType, cafeClass, cafeOwner);
  }

  public CafeConstructor(Location loc, String label, int conIx, IType conType, String javaSafeName,
      String javaClassName, String javaConSig, String javaOwner)
  {
    super(loc, label, conIx, null, conType, javaSafeName, javaClassName, javaConSig, javaOwner);
  }
}
