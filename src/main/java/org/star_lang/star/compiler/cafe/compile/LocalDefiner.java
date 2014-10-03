package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

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
public class LocalDefiner implements Definer
{
  protected final LabelNode endLabel;
  protected final CodeContext ccxt;
  protected final CodeCatalog bldCat;

  public LocalDefiner(LabelNode endLabel, CodeContext ccxt)
  {
    this.endLabel = endLabel;
    this.ccxt = ccxt;
    this.bldCat = ccxt.getBldCat();
  }

  @Override
  public VarInfo declareArg(Location loc, String name, int varOffset, IType varType, CafeDictionary dict,
      AccessMode access, boolean isInited, ErrorReport errors)
  {
    MethodNode mtd = ccxt.getMtd();

    ISpec vrSpec = SrcSpec.generic(loc, varType, dict, ccxt.getRepository(), errors);

    VarInfo var = dict.declareLocal(name, vrSpec, isInited, access);

    if (isInited) {
      LabelNode startLabel = new LabelNode();
      mtd.localVariables.add(new LocalVariableNode(var.getJavaSafeName(), var.getJavaSig(), null, startLabel, endLabel,
          var.getOffset()));
      InsnList ins = mtd.instructions;
      ins.add(startLabel);
    }
    return var;
  }
}