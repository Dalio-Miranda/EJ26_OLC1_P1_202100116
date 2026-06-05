package com.golite.ast;

public interface Visitor {
    Object visit(ProgramNode node, Environment env);
    Object visit(VarDeclNode node, Environment env);
    Object visit(AssignNode node, Environment env);
    Object visit(BinaryOpNode node, Environment env);
    Object visit(UnaryOpNode node, Environment env);
    Object visit(LiteralNode node, Environment env);
    Object visit(IdentifierNode node, Environment env);
    Object visit(IfNode node, Environment env);
    Object visit(ForNode node, Environment env);
    Object visit(BlockNode node, Environment env);
    Object visit(BreakNode node, Environment env);
    Object visit(ContinueNode node, Environment env);
    Object visit(PrintlnNode node, Environment env);
    Object visit(AtoiNode node, Environment env);
    Object visit(ParseFloatNode node, Environment env);
    Object visit(TypeOfNode node, Environment env);
    Object visit(FuncCallNode node, Environment env);
    Object visit(CompoundAssignNode node, Environment env);
    Object visit(IncrDecrNode node, Environment env);
}