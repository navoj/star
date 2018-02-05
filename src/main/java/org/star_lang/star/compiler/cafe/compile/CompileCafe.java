package org.star_lang.star.compiler.cafe.compile;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.splitlarge.NumberedTextifier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.code.CafeCode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.cont.NullCont;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.ApplicationProperties;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;

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
public class CompileCafe {
  static final boolean CHECK_BYTECODE = ApplicationProperties.getProperty("CHECK_BYTECODE", false);

  /**
   * Compile Cafe content as presented in a list of abstract syntax terms.
   * <p>
   * Typically, one of the content items will be a package function. This function encapsulates the
   * true code component.
   *
   * @param src        the uri of the source that results in this content
   * @param repository code repository
   * @param path       munge prefix
   * @param pkgFunName the NAME of the package function to construct the package
   * @param loc        The master location for this package
   * @param defs       A list of definitions of types and functions that make up the content
   * @param bldCat     The generated code is put into a build catalog
   * @param errors     A source catalog to access for imports needed by this program.
   */
  public static void compileContent(ResourceURI src, CodeRepository repository, String path, String pkgFunName,
                                    Location loc, IArray defs, CodeCatalog bldCat, ErrorReport errors) {
    try {
      String owner = extendPath(path, Names.PKG);
      CodeCatalog codeCatalog = (CodeCatalog) bldCat.fork(StandardNames.COMPILED);

      final CafeManifest manifest = new CafeManifest(src, path);
      final ClassRoot classRoot = new ClassRoot(path, pkgFunName);

      // We construct a static class, with static initializers for the package
      ClassNode program = new ClassNode();

      program.version = Opcodes.V1_6;
      program.access = Opcodes.ACC_PUBLIC;
      program.name = owner;
      program.superName = Utils.javaInternalClassName(Object.class);
      program.sourceFile = loc.getSrc();

      CafeDictionary dict = new CafeDictionary(path, program);

      HWM initHwm = new HWM();

      MethodNode initMtd = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "<clinit>", "()V", null,
          new String[]{});

      InsnList ins = initMtd.instructions;
      LabelNode endLabel = new LabelNode();

      CodeContext ccxt = new CodeContext(repository, program, initMtd, initHwm, initMtd, initHwm, dict.getLocalAvail(),
          codeCatalog, new NullCont(), errors, endLabel, "<clinit>", dict, dict);

      // Define the definitions
      Theta.compileDefinitions(defs, endLabel, new LocalDefiner(ccxt), new BuildProgram(
          owner, dict, program, initMtd, initHwm, manifest, loc), loc, ccxt);

      ins.add(endLabel);

      ccxt.installInitMtd();

      genByteCode(owner, loc, program, codeCatalog, errors);
      addCatalogEntry(bldCat, Names.CAFE_MANIFEST, manifest);
      addCatalogEntry(bldCat, Names.CLASS_ROOT, classRoot);
    } catch (RepositoryException e) {
      errors.reportError("could not add compiled code package to build catalog", loc);
    }
  }

  private static class BuildProgram implements Theta.IThetaBody {
    private final String owner;
    private final ClassNode program;
    private final MethodNode mtd;
    private final HWM hwm;
    private final CafeManifest manifest;
    private final CafeDictionary dict;
    private final Location loc;

    BuildProgram(String owner, CafeDictionary dict, ClassNode program, MethodNode mtd, HWM stackHWM,
                 CafeManifest manifest, Location loc) {
      this.owner = owner;
      this.dict = dict;
      this.program = program;
      this.mtd = mtd;
      this.hwm = stackHWM;
      this.manifest = manifest;
      this.loc = loc;
    }

    @Override
    public ISpec compile(CafeDictionary thetaDict, CodeCatalog bldCat, ErrorReport errors, CodeRepository repository) {
      InsnList ins = mtd.instructions;

      // set up static initializers for any built-ins
      for (Entry<String, Inliner> entry : dict.getBuiltinReferences().entrySet()) {
        Inliner inline = entry.getValue();

        int mark = hwm.getDepth();
        inline.inline(program, mtd, hwm, loc);
        hwm.reset(mark);
      }

      for (Entry<String, VarInfo> entry : thetaDict.allEntries().entrySet()) {
        VarInfo var = entry.getValue();

        switch (var.getWhere()) {
          case localVar: {
            String javaTypeSig = var.getJavaSig();
            String javaSafeName = var.getJavaSafeName();

            if (!Theta.addField(program, javaSafeName, javaTypeSig, Opcodes.ACC_STATIC)) {
              var.loadValue(mtd, hwm, thetaDict);

              ins.add(new FieldInsnNode(Opcodes.PUTSTATIC, owner, javaSafeName, javaTypeSig));
            }

            VarInfo pkgVar = new VarInfo(var.getLoc(), var.getName(), true, VarSource.staticField, null, var.getKind(),
                -1, AccessMode.readOnly, var.getType(), javaSafeName, null, var.getJavaType(), var.getJavaSig(), var
                .getJavaInvokeSig(), var.getJavaInvokeName(), owner);
            manifest.addDefinition(pkgVar);
          }
          default:
        }
      }

      mtd.maxLocals = thetaDict.getLocalHWM();
      mtd.maxStack = hwm.getHwm();
      if (thetaDict.allEntries().containsKey(Names.MAIN))
        buildMainClosure(program, thetaDict.find(Names.MAIN), errors);

      for (ITypeDescription desc : thetaDict.allTypes())
        manifest.redefineType((CafeTypeDescription) desc);
      for (ResourceURI imp : thetaDict.getImports())
        manifest.addImport(imp);

      return null;
    }

    @Override
    public void introduceType(CafeTypeDescription type) {
      manifest.addType(type);
    }
  }

  // Build the classic void main(String[] args){ main(arg[0]cast string, etc. }
  // procedure, use it to call the Cafe main with appropriate type casting code
  // generated
  private static void buildMainClosure(ClassNode program, VarInfo mainVar, ErrorReport errors) {
    MethodNode main = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, Names.MAIN, "([Ljava/lang/String;)V",
        null, new String[]{});

    program.methods.add(main);

    LabelNode mainFirstLabel = new LabelNode();
    LabelNode mainEndLabel = new LabelNode();
    main.instructions.add(mainFirstLabel);

    main.localVariables
        .add(new LocalVariableNode("args", "[Ljava/lang/String;", null, mainFirstLabel, mainEndLabel, 0));

    IType argTypes[] = TypeUtils.getProcedureArgTypes(mainVar.getType());

    HWM hwm = new HWM();

    main.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, program.name, Names.MAIN, mainVar.getJavaSig()));
    hwm.bump(1);

    for (int ix = 0; ix < argTypes.length; ix++) {
      IType argType = argTypes[ix];
      switch (Types.varType(argType)) {
        case rawBool: // Generate equivalent of "arg is Boolean.getBoolean(arg)"
          main.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
          main.instructions.add(new LdcInsnNode(ix + 1));
          main.instructions.add(new InsnNode(Opcodes.AALOAD));
          main.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "getBoolean",
              "(Ljava/lang/String;)Z"));
          hwm.bump(1);
          break;
        case rawInt: // Generate equivalent of "arg is Long.parseInt(arg)"
          main.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
          main.instructions.add(new LdcInsnNode(ix + 1));
          main.instructions.add(new InsnNode(Opcodes.AALOAD));
          main.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "parseInt",
              "(Ljava/lang/String;)I"));
          hwm.bump(1);
          break;
        case rawLong: // Generate equivalent of "arg is Long.parseInteger(arg)"
          main.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
          main.instructions.add(new LdcInsnNode(ix + 1));
          main.instructions.add(new InsnNode(Opcodes.AALOAD));
          main.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Long", "parseLong",
              "(Ljava/lang/String;)J"));
          hwm.bump(1);
          break;
        case rawFloat:
          main.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
          main.instructions.add(new LdcInsnNode(ix + 1));
          main.instructions.add(new InsnNode(Opcodes.AALOAD));
          main.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "parseDouble",
              "(Ljava/lang/String;)D"));
          break;
        case rawString:
          main.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
          main.instructions.add(new LdcInsnNode(ix + 1));
          main.instructions.add(new InsnNode(Opcodes.AALOAD));

          hwm.bump(1);
          break;
        case general:
          errors.reportError("invalid type for a main program argument: " + argType, mainVar.getLoc());
          break;
        default:
          errors.reportError("invalid type for a main program argument: " + argType, mainVar.getLoc());
          break;
      }
    }

    main.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, mainVar.getJavaType(), Names.ENTER, mainVar
        .getJavaInvokeSig()));
    main.maxStack = hwm.getHwm() + 1;
    main.maxLocals = 2;

    main.instructions.add(new InsnNode(Opcodes.RETURN));
    main.instructions.add(mainEndLabel);
  }

  static void pkgImport(CodeRepository repository, IAbstract def, CafeDictionary dict, ErrorReport errors) {
    if (CafeSyntax.isImport(def)) {
      String pkgRef = CafeSyntax.importImport(def);
      Location loc = def.getLoc();

      try {
        ResourceURI uri = URIUtils.parseUri(pkgRef);

        CafeManifest manifest = locateManifest(repository, loc, uri, errors);

        if (manifest != null) {
          dict.addEntries(manifest.getDefs());

          importPkgTypes(new HashSet<>(), uri, loc, repository, dict, errors);
          dict.addImport(uri);
        } else
          errors.reportError("expecting manifest for " + pkgRef, loc);
      } catch (ResourceException e) {
        errors.reportError("invalid uri: " + pkgRef + "\nbecause " + e.getMessage(), loc);
      }
    } else
      errors.reportError("expecting an import specification", def.getLoc());
  }

  private static void importPkgTypes(Set<ResourceURI> processed, ResourceURI uri, Location loc,
                                     CodeRepository repository, CafeDictionary dict, ErrorReport errors) {
    CafeManifest manifest = locateManifest(repository, loc, uri, errors);

    if (manifest != null) {
      dict.importTypes(manifest.getTypes());
      for (ResourceURI imported : manifest.getImports()) {
        if (!processed.contains(imported)) {
          processed.add(imported);
          importPkgTypes(processed, imported, loc, repository, dict, errors);
        }
      }
    }
  }

  public static CafeManifest locateManifest(CodeRepository repository, Location loc, ResourceURI uri, ErrorReport errors) {
    CodeTree codeCatalog = repository.findCode(uri);

    if (codeCatalog instanceof CodeCatalog) {
      try {
        CodeTree manifestEntry = ((CodeCatalog) codeCatalog).resolve(Names.CAFE_MANIFEST, CafeManifest.EXTENSION);
        if (manifestEntry instanceof CafeManifest)
          return (CafeManifest) manifestEntry;
      } catch (RepositoryException e) {
        errors.reportError("cannot access " + uri + "\nbecause " + e.getMessage(), loc);
      }
    } else
      errors.reportError("cannot access " + uri, loc);

    return null;
  }

  public static void genByteCode(String name, Location loc, ClassNode klass, CodeCatalog bldCat, ErrorReport errors) {
    if (StarCompiler.SHOWBYTECODE) {
      PrintWriter printer = new PrintWriter(System.out);
      ClassVisitor tcv = new TraceClassVisitor(null, new NumberedTextifier(), printer);

      klass.accept(tcv);
    }

    if (errors.isErrorFree()) {
      try {
        ClassWriter cw = new CafeClassWriter();

        klass.accept(cw);
        byte code[] = cw.toByteArray();

        if (CHECK_BYTECODE) {
          ClassReader cr = new ClassReader(code);

          cr.accept(new CheckClassAdapter(new ClassNode(), true), 0);
        }

        addCatalogEntry(bldCat, klass.name, new CafeCode(klass.name, code));
      } catch (RepositoryException e) {
        errors.reportError("could not add compiled code for " + name + " to build catalog", loc);
      } catch (Exception e) {
        e.printStackTrace();
        errors.reportError("class " + name + " has problem: " + e.getMessage(), loc);
        PrintWriter printer = new PrintWriter(System.out);

        TraceClassVisitor tcv = new TraceClassVisitor(null, new NumberedTextifier(), printer);
        CheckClassAdapter cc = new CheckClassAdapter(tcv);
        klass.accept(cc);
      }
    }
  }

  // Add an entry to the code catalog.
  static void addCatalogEntry(CodeCatalog cat, String name, CodeTree code) throws RepositoryException {
    cat.addCodeEntry(name, code);
  }

  static String extendPath(String path, String member) {
    return path + "/" + member;
  }
}
