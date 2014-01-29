package org.star_lang.star.compiler.cafe.compile;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.StringIterator;
import org.star_lang.star.compiler.util.StringSequence;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class Utils
{
  public static String javaInternalClassName(Class<?> klass)
  {
    return javaInternalName(klass.getName());
  }

  public static String javaInternalName(String name)
  {
    String replace = name.replace('.', '/');
    return replace;
  }

  public static String javaTypeSig(Class<?> klass)
  {
    StringBuilder blder = new StringBuilder();
    blder.append("L");
    blder.append(klass.getName().replace('.', '/'));
    blder.append(";");
    return blder.toString();
  }

  public static String javaPublicName(String internalName)
  {
    return internalName.replace('/', '.');
  }

  public static String javaInvokeSig(Class<?> klass, String method)
  {
    for (Method mtd : klass.getMethods()) {
      if (mtd.getName().equals(method))
        return Type.getMethodDescriptor(mtd);
    }

    throw new IllegalArgumentException(method + " not a method in class " + klass.getName());
  }

  public static String javaInvokeSig(Method method)
  {
    return Type.getMethodDescriptor(method);
  }

  public static String javaIdentifierOf(String label)
  {
    StringBuilder b = new StringBuilder();
    int px = 0;
    int mx = label.length();

    if (px < mx) {
      int ch = label.codePointAt(px);

      if (Character.isJavaIdentifierStart(ch))
        b.appendCodePoint(ch);
      else if (Character.isJavaIdentifierPart(ch)) {
        b.append('_');
        b.appendCodePoint(ch);
      } else
        b.append(alphaName(ch));

      px = label.offsetByCodePoints(px, 1);

      while (px < mx) {
        ch = label.codePointAt(px);
        if (Character.isJavaIdentifierPart(ch))
          b.appendCodePoint(ch);
        else
          b.append(alphaName(ch));
        px = label.offsetByCodePoints(px, 1);
      }
    }
    return b.toString();
  }

  public static String javaPath(String pth)
  {
    StringBuilder blder = new StringBuilder();

    for (StringIterator it = new StringIterator(pth); it.hasNext();) {
      int ch = it.next();

      if (ch == '/')
        blder.append("/");
      else if (Character.isJavaIdentifierPart(ch))
        blder.appendCodePoint(ch);
      else
        blder.append(alphaName(ch));
    }
    return blder.toString();
  }

  static private String alphaName(int ch)
  {
    switch (ch) {
    case '!':
      return "$bang$";
    case '@':
      return "$at$";
    case '#':
      return "$hash$";
    case '%':
      return "$pcnt$";
    case '^':
      return "$hat$";
    case '&':
      return "$amp";
    case '*':
      return "$star$";
    case '(':
      return "$lpar$";
    case ')':
      return "$rpar$";
    case '[':
      return "$lbra$";
    case ']':
      return "$rbra$";
    case '{':
      return "$lbrc$";
    case '}':
      return "$rbrc$";
    case '<':
      return "$lngl$";
    case '>':
      return "$rngl$";
    case '-':
      return "$mnus$";
    case '+':
      return "$plus$";
    case '=':
      return "$eql$";
    case '|':
      return "$bar$";
    case '\\':
      return "$bsl$";
    case '/':
      return "$slsh$";
    case ';':
      return "$semi$";
    case ':':
      return "$coln$";
    case '\'':
      return "$sqte$";
    case '\"':
      return "$dqte$";
    case ',':
      return "$cmma$";
    case '.':
      return "$dot$";
    case '?':
      return "$qury$";
    case '`':
      return "$tick$";
    case '~':
      return "$tild$";
    case '$':
      return "$";
    default:
      return "_";
    }
  }

  public static void jumpTarget(InsnList ins, LabelNode lbl)
  {
    AbstractInsnNode in = ins.getLast();
    while (in != null) {
      if (in instanceof JumpInsnNode) {
        JumpInsnNode jump = (JumpInsnNode) in;
        if (jump.label == lbl) {
          in = in.getPrevious();
          ins.remove(jump); // remove redundant jump
          continue;
        }

        break;
      } else if (in instanceof LabelNode)
        in = in.getPrevious();
      else
        break;
    }
    ins.add(lbl);
  }

  public static boolean isAnonymous(String label)
  {
    if (label.startsWith(StandardNames.ANONYMOUS_PREFIX)) {
      for (StringSequence it = new StringSequence(label, StandardNames.ANONYMOUS_PREFIX.length()); it.hasNext();) {
        int next = it.next();
        if (!Character.isDigit(next) && next != '_')
          return false;
      }
      return true;
    }
    return false;
  }

  public static boolean noNulls(Object[] test)
  {
    for (Object o : test)
      if (o == null)
        return false;
    return true;
  }
}
