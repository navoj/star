package org.star_lang.star.compiler.cafe.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.operators.Intrinsics;

@SuppressWarnings("serial")
public class CafeTypeDescription extends TypeDescription implements ICafeTypeDescription
{
  final private String javaName;
  final private String javaSig;
  final private Map<String, AttSpec> atts = new HashMap<>();

  public CafeTypeDescription(Location loc, IType type)
  {
    super(loc, type);
    this.javaName = null;
    this.javaSig = null;
  }

  /**
   * Construct a type description with a list of value specifiers. This should be the normal way
   * that a type description is created when all the value specifiers are known.
   * 
   * It is an error for there to be more than one non-algebraic value specifier in the list; nor may
   * algebraic specifiers be mixed with non-algebraic specifiers.
   * 
   * A non-algebraic specifier is one such as a function specifier, or a scalar specifier.
   * 
   * @param loc
   *          the defining location for this type
   * @param type
   *          the type being defined. Should be a quantified type expression in the case that the
   *          type is generic.
   * @param javaName
   *          the NAME of the java type that models this type.
   * @param constructors
   *          the list of constructors for the type.
   */
  public CafeTypeDescription(Location loc, IType type, String javaName, List<IValueSpecifier> constructors)
  {
    super(loc, type, constructors);
    this.javaName = Utils.javaInternalName(javaName);
    this.javaSig = "L" + this.javaName + ";";
  }

  public CafeTypeDescription(IType type, String javaName)
  {
    super(type);
    this.javaName = Utils.javaInternalName(javaName);
    this.javaSig = "L" + this.javaName + ";";
  }

  @Override
  public String getJavaName()
  {
    return javaName;
  }

  @Override
  public String getJavaSig()
  {
    return javaSig;
  }

  /*
   * Manage the available constructors for this type
   */
  @Override
  public IValueSpecifier declareConstructor(String name, IType conType, int conIx, ISpec spec, String javaTypeName,
      String javaOwner, String javaConSig, String javaSafeName, List<ISpec> fields, SortedMap<String, Integer> index)
      throws TypeConstraintException
  {
    IValueSpecifier vSpec = getValueSpecifier(name);
    if (vSpec != null)
      throw new TypeConstraintException("attempt to redeclare constructor: " + name);
    else {
      for(Entry<String,Integer> e:index.entrySet()){
        declareAtt(e.getKey(),fields.get(e.getValue()));
      }

      IValueSpecifier record = new CafeRecordSpecifier(getLoc(), name, conIx, javaTypeName, javaOwner, javaConSig,
          javaSafeName, index, conType);
      valueSpecifiers.add(record);
      return record;
    }
  }

  @Override
  public IValueSpecifier declareConstructor(String name, IType conType, int conIx, String javaTypeName,
      String javaOwner, String javaConSig, String javaSafeName) throws TypeConstraintException
  {
    IValueSpecifier spec = getValueSpecifier(name);
    if (spec != null)
      throw new TypeConstraintException("attempt to redeclare constructor: " + name);
    else
      valueSpecifiers.add(spec = new ConstructorSpecifier(getLoc(), name, conIx, null, conType, javaSafeName,
          javaTypeName, javaConSig, javaOwner));
    return spec;
  }

  /*
   * Manage the getters and setters of the various fields of this type
   */

  protected static class AttSpec extends SrcSpec
  {
    private final String javaFieldName;

    AttSpec(String name, String javaType, String javaSig, String javaInvokeSig, String javaInvokeName, IType type,
        Location loc)
    {
      super(type, loc, javaType, javaSig, javaInvokeSig, javaInvokeName);
      this.javaFieldName = Utils.javaIdentifierOf(name);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendId(javaFieldName);
      disp.append(":");
      super.prettyPrint(disp);
    }
  }

  public String javaFieldType(String att)
  {
    assert atts.containsKey(att);
    return atts.get(att).getJavaType();
  }

  public String fieldJavaName(String att)
  {
    assert atts.containsKey(att);
    return atts.get(att).javaFieldName;
  }

  public String getJavaFieldSig(String att)
  {
    assert atts.containsKey(att);
    return atts.get(att).getJavaSig();
  }

  private void declareAtt(String att, ISpec field) throws TypeConstraintException
  {
    AttSpec spec = atts.get(att);
    if (spec == null)
      atts.put(att, new AttSpec(att, field.getJavaType(), field.getJavaSig(), field.getJavaInvokeSig(), field
          .getJavaInvokeName(), field.getType(), field.getLoc()));
    else {
      // Make sure its the same type
      TypeUtils.unify(spec.getType(), field.getType(), field.getLoc(), Intrinsics.intrinsics());
    }
  }

  public ISpec getFieldSpec(String att)
  {
    return atts.get(att);
  }

  public boolean hasAttribute(String att)
  {
    return atts.containsKey(att);
  }

  public CafeTypeDescription cleanCopy()
  {
    List<IValueSpecifier> specs = new ArrayList<>();
    for (IValueSpecifier spec : getValueSpecifiers()) {
      if (spec instanceof ICafeConstructorSpecifier)
        specs.add((IValueSpecifier) ((ICafeConstructorSpecifier) spec).cleanCopy());
      else
        specs.add(spec);
    }
    return new CafeTypeDescription(getLoc(), type, getJavaName(), specs);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(getName());
    disp.append(" is ");
    disp.append(StandardNames.TYPE);
    int mark = disp.markIndent(2);
    disp.append("{\n");
    disp.append("javaName is ");
    disp.appendQuoted(javaName);
    disp.append(";\n");
    super.prettyPrint(disp);
    disp.append("\n");
    disp.popIndent(mark);
    disp.append("}\n");
  }

}
