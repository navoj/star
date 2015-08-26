package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.Intrinsics;

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
 */

/**
 * Version of the dictionary for use during code generation
 * 
 * @author fgm
 * 
 */
@SuppressWarnings("serial")
public class CafeDictionary implements ITypeContext
{
  private final LayeredMap<String, VarInfo> entries;
  private final LayeredMap<String, ITypeDescription> types;
  private final Map<String, Inliner> references;
  private final Set<ResourceURI> imports;
  private final LiveMap localAvail;
  private int freeOffset = 0;
  private final ClassNode owner;
  private final String path;
  private final CafeDictionary parent; // Used for resolving free variables

  private static final Map<String, VarInfo> root = new HashMap<>();

  static {
    for (ICafeBuiltin builtin : Intrinsics.allBuiltins()) {
      String name = builtin.getName();
      root.put(name, new VarInfo(Location.nullLoc, name, true, builtin.isStatic() ? VarSource.staticMethod
          : VarSource.literal, null, JavaKind.builtin, 0, AccessMode.readOnly, builtin.getType(),
          builtin.getJavaName(), null, builtin.getJavaType(), builtin.getJavaSig(), builtin.getJavaInvokeSignature(),
          builtin.getJavaInvokeName(), Type.getInternalName(builtin.getClass())));
    }
    for (ITypeDescription desc : Intrinsics.builtinTypes()) {
      if (desc instanceof IAlgebraicType) {
        for (IValueSpecifier spec : ((IAlgebraicType) desc).getValueSpecifiers()) {
          ConstructorSpecifier cSpec = (ConstructorSpecifier) spec;
          String name = spec.getLabel();
          VarInfo conVar = root.get(name);
          if (conVar == null) {
            VarInfo var = new VarInfo(Location.nullLoc, name, true, VarSource.literal, null, JavaKind.constructor, -1,
                AccessMode.readOnly, spec.getConType(), cSpec.getJavaSafeName(), null, cSpec.getJavaType(), "L"
                    + cSpec.getJavaType() + ";", cSpec.getJavaConSig(), Types.INIT, cSpec.getJavaOwner());

            root.put(cSpec.getLabel(), var);
          }
        }
      }
    }
  }

  public CafeDictionary(String path, ClassNode owner)
  {
    this.entries = new LayeredHash<>(root);
    this.types = new LayeredHash<>();
    this.references = new HashMap<>();
    this.imports = new TreeSet<>();
    this.owner = owner;
    this.path = path;
    localAvail = new LiveMap();

    ITypeContext intrinsics = Intrinsics.intrinsics();

    for (Entry<String, ITypeDescription> entry : intrinsics.getAllTypes().entrySet())
      defineType(entry.getValue());

    this.parent = null;
  }

  private CafeDictionary(String path, ClassNode owner, LayeredMap<String, VarInfo> entries,
      LayeredMap<String, ITypeDescription> types, Map<String, Inliner> references, Set<ResourceURI> imports,
      LiveMap localAvail, CafeDictionary parent)
  {
    this.entries = entries;
    this.types = types;
    this.references = references;
    this.owner = owner;
    this.localAvail = localAvail;
    this.path = path;
    this.imports = imports;
    this.parent = parent;
  }

  public CafeDictionary funDict(ClassNode owner)
  {
    return funDict(path + "/" + owner.name, owner);
  }

  public CafeDictionary funDict(String path, ClassNode owner)
  {
    LayeredMap<String, ITypeDescription> forkedTypes = types.fork();
    LayeredMap<String, VarInfo> subEntries = new LayeredHash<>();
    // ForkFilter filter = new FreeFilter();
    //
    // for (Entry<String, VarInfo> entry : this.entries.entrySet())
    // if (filter.verify(entry.getValue()))
    // subEntries.put(entry.getKey(), entry.getValue());

    // rootConstructors(subEntries, types.values());

    // Get the constructor definitions
    return new CafeDictionary(path, owner, subEntries, forkedTypes, new HashMap<>(), imports,
        new LiveMap(), this);
  }

  @Override
  public CafeDictionary fork()
  {
    return new CafeDictionary(path, owner, entries.fork(), types, references, imports, localAvail, parent);
  }

  public interface ForkFilter
  {
    boolean verify(VarInfo var);
  }

  public CafeDictionary getParent()
  {
    return parent;
  }

  public VarInfo reserve(Location loc, String name, boolean isInited, String owner, String javaType,
      String javaSignature, String javaInvokeSig, String javaInvokeName, IType type, AccessMode access,
      VarSource where, JavaKind kind)
  {
    final int offset;
    switch (where) {
    case localVar:
      switch (kind) {
      case rawBool:
      case rawChar:
      case rawInt:
      case general:
      case rawBinary:
      case rawString:
      case rawDecimal:
        offset = localAvail.reserve(1);
        break;
      case rawLong:
      case rawFloat:
        offset = localAvail.reserve(2);
        break;
      default:
        offset = 0;
      }
      break;

    case freeVar:
      switch (kind) {
      case rawBool:
      case rawChar:
      case rawInt:
      case general:
      case rawBinary:
      case rawString:
      case rawDecimal:
        offset = freeOffset++;
        break;
      case rawLong:
      case rawFloat:
        offset = freeOffset += 2;
        break;
      default:
        offset = 0;
      }
      break;
    default:
      offset = 0;
    }
    return new VarInfo(loc, name, isInited, where, null, kind, offset, access, type, Utils.javaIdentifierOf(name),
        null, javaType, javaSignature, javaInvokeSig, javaInvokeName, owner);
  }

  public static int varSlotCount(IType type)
  {
    if (TypeUtils.isRawFloatType(type))
      return 2;
    else if (TypeUtils.isRawIntType(type))
      return 1;
    else if (TypeUtils.isRawLongType(type))
      return 2;
    else
      return 1;
  }

  public VarInfo reserveLocal(String name, ISpec desc, boolean isInited, AccessMode access)
  {
    return reserve(desc.getLoc(), name, isInited, owner.name, desc.getJavaType(), desc.getJavaSig(), desc
        .getJavaInvokeSig(), desc.getJavaInvokeName(), desc.getType(), access, VarSource.localVar, Types.varType(desc
        .getType()));
  }

  public VarInfo declareLocal(Location loc, String name, boolean isInited, IType type, String javaType,
      String javaSignature, String javaInvokeSig, String javaInvokeName, AccessMode access)
  {
    return declare(name, reserve(loc, name, isInited, owner.name, javaType, javaSignature, javaInvokeSig,
        javaInvokeName, type, access, VarSource.localVar, Types.varType(type)));
  }

  public VarInfo declareLocal(String name, ISpec desc, boolean isInited, AccessMode access)
  {
    IType type = desc.getType();
    return declare(name, reserve(desc.getLoc(), name, isInited, owner.name, desc.getJavaType(), desc.getJavaSig(), desc
        .getJavaInvokeSig(), desc.getJavaInvokeName(), type, access, VarSource.localVar, Types.varType(type)));
  }

  public VarInfo declare(String name, VarInfo var)
  {
    entries.put(name, var);
    if (var.getWhere() == VarSource.localVar)
      localAvail.allocInt(var.getOffset(), varSlotCount(var.getType()));

    return var;
  }

  public Map<String, VarInfo> allEntries()
  {
    return entries;
  }

  public void addEntries(Map<String, VarInfo> imps)
  {
    entries.putAll(imps);
  }

  @Override
  public Map<String, ITypeDescription> getAllTypes()
  {
    return types;
  }

  public Collection<VarInfo> getFreeVars()
  {
    List<VarInfo> free = new ArrayList<>();
    for (Entry<String, VarInfo> entry : entries.entrySet()) {
      VarInfo var = entry.getValue();
      if (var.getWhere() == VarSource.freeVar)
        free.add(var);
    }
    return free;
  }

  public void migrateFreeVars(CafeDictionary fork)
  {
    for (Entry<String, VarInfo> entry : fork.entries.entrySet()) {
      String varName = entry.getKey();
      VarInfo var = entry.getValue();
      if (var != null && var.getWhere() == VarSource.freeVar) {
        VarInfo free = entries.get(varName);
        if (free == null || free != var)
          entries.put(varName, var);
      }
    }
  }

  public void addReference(String name, Inliner init)
  {
    // pushInline(name, references.get(name));
    references.put(name, init);
  }

  public Map<String, Inliner> getBuiltinReferences()
  {
    return references;
  }

  public void addImport(ResourceURI pkg)
  {
    imports.add(pkg);
  }

  public Set<ResourceURI> getImports()
  {
    return imports;
  }

  public int getLocalHWM()
  {
    return localAvail.getHwm();
  }

  public LiveMap getLocalAvail()
  {
    return localAvail;
  }

  public VarInfo declareFree(AccessMode access, VarInfo ref)
  {
    assert !entries.containsKey(ref.getName());
    VarInfo var = reserve(ref.getLoc(), ref.getName(), true, owner.name, ref.getJavaType(), ref.getJavaSig(), ref
        .getJavaInvokeSig(), ref.getJavaInvokeName(), ref.getType(), access, VarSource.freeVar, ref.getKind());
    entries.put(var.getName(), var);
    return var;
  }

  public VarInfo find(String name)
  {
    return entries.get(name);
  }

  public ITypeDescription findType(String name)
  {
    return types.get(name);
  }

  public void importTypes(Map<String, CafeTypeDescription> types)
  {
    this.types.putAll(types);
  }

  public boolean hasAttribute(IType type, String att)
  {
    ITypeDescription desc = findType(type.typeLabel());
    if (desc instanceof TypeDescription)
      return ((CafeTypeDescription) desc).hasAttribute(att);
    else
      return false;
  }

  public ISpec getFieldSpec(IType type, String att)
  {
    ITypeDescription desc = findType(type.typeLabel());
    if (desc instanceof TypeDescription)
      return ((CafeTypeDescription) desc).getFieldSpec(att);
    else
      return null;
  }

  public String javaFieldSig(IType type, String att)
  {
    CafeTypeDescription desc = (CafeTypeDescription) findType(type.typeLabel());
    if (desc != null)
      return desc.getJavaFieldSig(att);
    else
      return null;
  }

  public String getPath()
  {
    return path;
  }

  public ClassNode getOwner()
  {
    return owner;
  }

  public String getOwnerName()
  {
    return owner.name;
  }

  public TypeDescription declareType(Location loc, IType type, String javaName)
  {
    CafeTypeDescription desc = new CafeTypeDescription(loc, type, javaName, new ArrayList<>());
    defineType(desc);
    return desc;
  }

  @Override
  public ITypeDescription getTypeDescription(String name)
  {
    return types.get(name);
  }

  @Override
  public boolean typeExists(String name)
  {
    return types.containsKey(name);
  }

  @Override
  public void defineType(ITypeDescription desc)
  {
    String name = desc.getName();
    types.put(name, desc);
  }

  public IValueSpecifier declareConstructor(Location loc, IType type, IType conType, String javaSafeName,
      String javaInvokeSig, String javaTypeName, String javaOwner, String name, int conIx, List<ISpec> fields,
      SortedMap<String, Integer> index, ErrorReport errors)
  {
    CafeTypeDescription desc = (CafeTypeDescription) findType(type.typeLabel());
    assert desc != null && TypeUtils.isConstructorType(conType);
    try {
      VarInfo var = new VarInfo(loc, name, true, VarSource.literal, null, JavaKind.constructor, -1,
          AccessMode.readOnly, conType, javaSafeName, null, javaTypeName, "L" + javaTypeName + ";", javaInvokeSig,
          Types.INIT, desc.getJavaName());
      declare(name, var);

      return desc.declareConstructor(name, conType, conIx, var, Utils.javaPublicName(javaTypeName), javaOwner,
          javaInvokeSig, javaSafeName, fields, index);
    } catch (TypeConstraintException e) {
      errors.reportError("invalid constructor: " + name + "\nbecause " + e.getMessage(), loc);
      return null;
    }
  }

  @Override
  public void declareConstructor(ConstructorSpecifier cons)
  {
    CafeTypeDescription desc = (CafeTypeDescription) findType(cons.getTypeLabel());
    assert desc != null;
    String name = cons.getLabel();
    desc.defineValueSpecifier(name, cons);
    declare(name, new VarInfo(cons.getLoc(), name, true, VarSource.literal, null, JavaKind.constructor, 0,
        AccessMode.readOnly, cons.getConType(), cons.getJavaSafeName(), null, cons.getJavaType(), "L"
            + cons.getJavaType() + ";", cons.getJavaConSig(), Types.INIT, desc.getJavaName()));
  }

  @Override
  public boolean isConstructor(String conName)
  {
    VarInfo var = entries.get(conName);
    if (var != null)
      return var.getKind() == JavaKind.constructor;
    else
      return false;
  }

  public boolean isDefined(String name)
  {
    return entries.containsKey(name);
  }

  private static IValueSpecifier findConstructor(String name, Collection<IValueSpecifier> specifiers)
  {
    for (IValueSpecifier spec : specifiers) {
      if (spec instanceof ConstructorSpecifier && spec.getLabel().equals(name))
        return spec;
    }
    return null;
  }

  public String getConstructorJavaSignature(String name)
  {
    VarInfo entry = entries.get(name);
    if (entry.getKind() == JavaKind.constructor)
      return entry.getJavaSig();

    return null;
  }

  public String fieldJavaName(IType type, String field)
  {
    type = TypeUtils.deRef(type);
    CafeTypeDescription desc = (CafeTypeDescription) findType(type.typeLabel());
    if (desc != null)
      return desc.fieldJavaName(field);
    else
      return Utils.javaIdentifierOf(field);
  }

  public String javaName(IType type)
  {
    type = TypeUtils.deRef(type);
    CafeTypeDescription desc = (CafeTypeDescription) findType(type.typeLabel());
    if (desc != null)
      return desc.getJavaName();
    else
      return Types.IVALUE;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    String sep = "";
    for (Entry<String, VarInfo> entry : entries.entrySet()) {
      switch (entry.getValue().getKind()) {
      case builtin:
      case constructor:
        break;
      default:
        disp.append(sep);
        sep = "\n";
        entry.getValue().prettyPrint(disp);
      }
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public IValueSpecifier getConstructor(String name)
  {
    for (Entry<String, ITypeDescription> entry : types.entrySet()) {
      ITypeDescription desc = entry.getValue();
      if (desc instanceof IAlgebraicType) {
        IValueSpecifier conDesc = findConstructor(name, ((IAlgebraicType) desc).getValueSpecifiers());
        if (conDesc instanceof ConstructorSpecifier)
          return conDesc;
      }
    }
    if (parent != null)
      return parent.getConstructor(name);
    else
      return null;
  }

  @Override
  public void defineTypeAlias(Location loc, ITypeAlias alias)
  {
    throw new UnsupportedOperationException("not permitted");
  }

  public Collection<ITypeDescription> allTypes()
  {
    List<ITypeDescription> allTypes = new ArrayList<>();
    for (Entry<String, ITypeDescription> entry : types.entrySet()) {
      if (parent == null) {
        if (Intrinsics.isIntrinsicType(entry.getKey()))
          continue;
      } else if (parent.isType(entry.getKey()))
        continue;
      allTypes.add(entry.getValue());
    }
    return allTypes;
  }

  private boolean isType(String name)
  {
    return types.containsKey(name);
  }

  @Override
  public TypeContract getContract(String name)
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public Map<String, TypeContract> allContracts()
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public void defineTypeContract(TypeContract contract)
  {
    throw new UnsupportedOperationException("not permitted");
  }
}
