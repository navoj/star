package org.star_lang.star.compiler.cafe.compile;

import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
/*
  * Copyright (c) 2015. Francis G. McCabe
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software distributed under the
  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied. See the License for the specific language governing
  * permissions and limitations under the License.
  */
public class CodeContext {
  private final CodeRepository repository;
  private final CodeCatalog bldCat;
  private final ClassNode klass;
  private final String inFunction;
  private final MethodNode mtd;
  private final HWM mtdHwm;
  private final MethodNode classInit;
  private final HWM clsHwm;
  private final LiveMap localMap;
  private final IContinuation valisCont;
  private final ErrorReport errors;
  private final LabelNode endLabel;
  private final CafeDictionary dict;
  private final CafeDictionary outer;

  public CodeContext(CodeRepository repository, ClassNode klass, MethodNode mtd, HWM mtdHwm, MethodNode classInit,
                     HWM initHwm, LiveMap localMap, CodeCatalog bldCat, IContinuation valisCont, ErrorReport errors,
                     LabelNode endLabel, String inFunction, CafeDictionary dict, CafeDictionary outer) {
    super();
    this.repository = repository;
    this.klass = klass;
    this.inFunction = inFunction;
    this.mtd = mtd;
    this.mtdHwm = mtdHwm;
    this.classInit = classInit;
    this.clsHwm = initHwm;
    this.localMap = localMap;
    this.bldCat = bldCat;
    this.valisCont = valisCont;
    this.errors = errors;
    this.endLabel = endLabel;
    this.dict = dict;
    this.outer = outer;
    assert localMap != null;
  }

  public CodeContext fork(ClassNode klass, MethodNode mtd, HWM mtdHwm, MethodNode classInit, HWM initHwm,
                          LiveMap localMap, String functionName) {
    return new CodeContext(repository, klass, mtd, mtdHwm, classInit, initHwm, localMap, bldCat, valisCont, errors, endLabel, functionName, dict, outer);
  }

  public CodeContext fork(ClassNode klass, MethodNode mtd, HWM hwm, LiveMap localMap, String funName, LabelNode endLabel) {
    return new CodeContext(repository, klass, mtd, hwm, new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
        Types.CLASS_INIT, Types.VOID_SIG, Types.VOID_SIG, new String[]{}), new HWM(), localMap, bldCat, valisCont, errors, endLabel, funName, dict, outer);
  }

  public CodeContext fork(IContinuation valisCont) {
    return new CodeContext(repository, klass, mtd, mtdHwm, classInit, clsHwm, localMap, bldCat, valisCont, errors, endLabel, inFunction, dict, outer);
  }

  public CodeContext fork(LabelNode endLabel) {
    return new CodeContext(repository, klass, mtd, mtdHwm, classInit, clsHwm, localMap, bldCat, valisCont, errors, endLabel, inFunction, dict, outer);
  }

  public CodeContext fork(CafeDictionary dict, CafeDictionary outer) {
    return new CodeContext(repository, klass, mtd, mtdHwm, classInit, clsHwm, localMap, bldCat, valisCont, errors, endLabel, inFunction, dict, outer);
  }

  public String getInFunction() {
    return inFunction;
  }

  public CodeRepository getRepository() {
    return repository;
  }

  public CodeCatalog getBldCat() {
    return bldCat;
  }

  public CodeCatalog getSynthCode() {
    return repository.synthCodeCatalog();
  }

  public ClassNode getKlass() {
    return klass;
  }

  public MethodNode getMtd() {
    return mtd;
  }

  public InsnList getIns() {
    return mtd.instructions;
  }

  public HWM getMtdHwm() {
    return mtdHwm;
  }

  public MethodNode getClassInit() {
    return classInit;
  }

  public HWM getClsHwm() {
    return clsHwm;
  }

  public IContinuation getValisCont() {
    return valisCont;
  }

  public ErrorReport getErrors() {
    return errors;
  }

  public LabelNode getEndLabel() {
    return endLabel;
  }

  public CafeDictionary getDict() {
    return dict;
  }

  public CafeDictionary getOuter() {
    return outer;
  }

  public void installInitMtd() {
    InsnList ins = classInit.instructions;

    if (realCode(ins)) {
      ins.add(new InsnNode(Opcodes.RETURN));
      classInit.maxStack = clsHwm.getHwm();
      classInit.maxLocals = localMap.getHwm();
      klass.methods.add(classInit);
    }
  }

  public static boolean realCode(InsnList ins) {
    for (Iterator<AbstractInsnNode> it = ins.iterator(); it.hasNext(); ) {
      AbstractInsnNode next = it.next();
      if (!(next instanceof LabelNode))
        return true;
    }
    return false;
  }
}
