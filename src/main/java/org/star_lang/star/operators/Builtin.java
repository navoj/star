package org.star_lang.star.operators;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.objectweb.asm.Type;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.data.type.IType;

/**
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
public class Builtin implements ICafeBuiltin
{
  protected final String name;
  protected final Class<?> implClass;
  protected final String javaClass;
  protected final String javaInvokeSig;
  protected final String javaInvokeName;
  protected final boolean isStatic;
  protected final IType type;

  public Builtin(String name, IType type)
  {
    this.name = name;
    this.implClass = getClass();
    this.javaClass = Type.getInternalName(implClass);
    this.javaInvokeSig = invokeSig(implClass);
    this.javaInvokeName = invokeName(implClass);
    this.isStatic = isStatic(implClass);
    this.type = type;
  }

  public Builtin(String name, IType type, Class<?> implClass)
  {
    this.name = name;
    this.implClass = implClass;
    this.javaClass = Type.getInternalName(implClass);
    this.javaInvokeSig = invokeSig(implClass);
    this.javaInvokeName = invokeName(implClass);
    this.isStatic = isStatic(implClass);
    this.type = type;
  }

  public Builtin(String name, IType type, Class<?> implClass, Method method)
  {
    this.name = name;
    this.type = type;
    this.isStatic = (method.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
    this.implClass = implClass;
    this.javaClass = Type.getInternalName(implClass);
    this.javaInvokeName = invokeName(method);
    this.javaInvokeSig = invokeSig(method);
  }

  @Override
  public IType getType()
  {
    return type;
  }

  private String invokeSig(Class<?> implClass)
  {
    for (Method m : implClass.getMethods()) {
      if (m.isAnnotationPresent(CafeEnter.class))
        return Type.getMethodDescriptor(m);
    }
    throw new IllegalStateException("no enter method supplied for " + name);
  }

  private String invokeSig(Method method)
  {
    return Type.getMethodDescriptor(method);
  }

  private String invokeName(Class<?> implClass)
  {
    for (Method m : implClass.getMethods()) {
      if (m.isAnnotationPresent(CafeEnter.class))
        return m.getName();
    }
    throw new IllegalStateException("no enter method supplied for " + name);
  }

  private String invokeName(Method method)
  {
    return method.getName();
  }

  private boolean isStatic(Class<?> implClass)
  {
    for (Method m : implClass.getMethods()) {
      if (m.isAnnotationPresent(CafeEnter.class))
        return (m.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
    }
    throw new IllegalStateException("no enter method supplied for " + name);
  }

  @Override
  public boolean isStatic()
  {
    return isStatic;
  }

  @Override
  public String getJavaInvokeSignature()
  {
    return javaInvokeSig;
  }

  @Override
  public String getJavaInvokeName()
  {
    return javaInvokeName;
  }

  @Override
  public String getJavaType()
  {
    return javaClass;
  }

  @Override
  public String getJavaSig()
  {
    return "L" + Utils.javaInternalClassName(implClass) + ";";
  }

  @Override
  public String getJavaName()
  {
    return Utils.javaIdentifierOf(name);
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Class<?> getImplClass()
  {
    return implClass;
  }
}
