package org.star_lang.star.compiler.canonical;

public interface TransformStatement<A, E, P, C, D, T>
{
  D transformContractDefn(ContractEntry con, T context);

  D transformContractImplementation(ImplementationEntry entry, T context);

  D transformImportEntry(ImportEntry entry, T context);

  D transformJavaEntry(JavaEntry entry, T context);

  D transformTypeAliasEntry(TypeAliasEntry entry, T context);

  D transformTypeEntry(TypeDefinition entry, T context);

  D transformVarEntry(VarEntry entry, T context);

  D transformOpenStatement(OpenStatement open, T context);

  D transformWitness(TypeWitness stmt, T context);
}
