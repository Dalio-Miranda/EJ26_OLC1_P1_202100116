package com.golite.ast;

/**
 * Interfaz Visitor para el patron de diseno Visitor.
 * Define un metodo visit() para cada tipo de nodo del AST.
 * El interprete implementa esta interfaz para ejecutar cada nodo.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public interface Visitor {
    // Nodos basicos
    Object visit(ProgramNode node, Environment env);
    Object visit(VarDeclNode node, Environment env);
    Object visit(AssignNode node, Environment env);
    Object visit(BinaryOpNode node, Environment env);
    Object visit(UnaryOpNode node, Environment env);
    Object visit(LiteralNode node, Environment env);
    Object visit(IdentifierNode node, Environment env);

    // Control de flujo
    Object visit(IfNode node, Environment env);
    Object visit(ForNode node, Environment env);
    Object visit(SwitchNode node, Environment env);
    Object visit(CaseNode node, Environment env);

    // Bloques y transferencia
    Object visit(BlockNode node, Environment env);
    Object visit(BreakNode node, Environment env);
    Object visit(ContinueNode node, Environment env);
    Object visit(ReturnNode node, Environment env);

    // Funciones embebidas
    Object visit(PrintlnNode node, Environment env);
    Object visit(AtoiNode node, Environment env);
    Object visit(ParseFloatNode node, Environment env);
    Object visit(TypeOfNode node, Environment env);

    // Llamadas a funciones
    Object visit(FuncCallNode node, Environment env);

    // Asignaciones
    Object visit(CompoundAssignNode node, Environment env);
    Object visit(IncrDecrNode node, Environment env);
}