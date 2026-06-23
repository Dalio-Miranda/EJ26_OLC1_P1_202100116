package com.golite.interpreter;

import com.golite.ast.*;
import java.util.ArrayList;

/**
 * Intérprete del lenguaje GoLite.
 * Implementa el patron Visitor para recorrer el AST
 * y ejecutar cada nodo segun su tipo.
 *
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class Interpreter implements Visitor {

    /* ===== EXCEPCIONES DE CONTROL DE FLUJO ===== */

    /**
     * Excepcion interna para manejar el break.
     */
    private static class BreakException extends RuntimeException {
        public BreakException() { super(); }
    }

    /**
     * Excepcion interna para manejar el continue.
     */
    private static class ContinueException extends RuntimeException {
        public ContinueException() { super(); }
    }

    /**
     * Excepcion interna para manejar el return.
     * Lleva el valor de retorno de la funcion.
     */
    public static class ReturnException extends RuntimeException {
        public Object valor;
        public ReturnException(Object valor) {
            super();
            this.valor = valor;
        }
    }

    /* ===== ESTADO DEL INTERPRETE ===== */

    // Lista de errores semanticos encontrados
    public ArrayList<String[]> errores = new ArrayList<>();

    // Buffer de salida para la consola
    public StringBuilder consola = new StringBuilder();

    // Nivel de anidamiento de bucles for
    private int nivelFor = 0;

    /* ===== METODOS DE UTILIDAD ===== */

    /**
     * Registra un error semantico
     */
    private void registrarError(String descripcion, int linea, int columna) {
        String[] error = {
            descripcion,
            String.valueOf(linea),
            String.valueOf(columna),
            "Semantico"
        };
        errores.add(error);
        consola.append("[ERROR SEMANTICO] ").append(descripcion)
               .append(" en linea ").append(linea)
               .append(", columna ").append(columna).append("\n");
    }

    /**
     * Obtiene el tipo GoLite de un valor Java
     */
    private String getTipo(Object valor) {
        if (valor == null)              return "nil";
        if (valor instanceof Integer)   return "int";
        if (valor instanceof Double)    return "float64";
        if (valor instanceof String)    return "string";
        if (valor instanceof Boolean)   return "bool";
        if (valor instanceof Character) return "rune";
        return "desconocido";
    }

    /**
     * Verifica si dos tipos son compatibles para asignacion
     */
    private boolean sonCompatibles(String tipoVar, String tipoVal) {
        if (tipoVar.equals(tipoVal)) return true;
        if (tipoVar.equals("float64") && tipoVal.equals("int")) return true;
        return false;
    }

    /**
     * Convierte Integer a Double para operaciones mixtas
     */
    private Object toDouble(Object valor) {
        if (valor instanceof Integer) return ((Integer) valor).doubleValue();
        return valor;
    }

    /* ===== IMPLEMENTACION DEL VISITOR ===== */

    /**
     * Visita el nodo raiz del programa
     */
    @Override
    public Object visit(ProgramNode node, Environment env) {
        for (Node stmt : node.statements) {
            stmt.accept(this, env);
        }
        return null;
    }

    /**
     * Visita una declaracion de variable
     */
    @Override
    public Object visit(VarDeclNode node, Environment env) {
        if (env.existsLocal(node.name)) {
            registrarError("La variable '" + node.name
                + "' ya fue declarada en este ambito", node.line, node.column);
            return null;
        }

        String tipo = node.type;
        Object valor = null;

        if (node.value != null) {
            valor = node.value.accept(this, env);
            String tipoValor = getTipo(valor);

            if (node.isImplicit) {
                tipo = tipoValor;
            } else {
                if (!sonCompatibles(tipo, tipoValor)) {
                    registrarError("No se puede asignar un valor de tipo '"
                        + tipoValor + "' a la variable de tipo '" + tipo + "'",
                        node.line, node.column);
                    return null;
                }
                if (tipo.equals("float64") && valor instanceof Integer) {
                    valor = ((Integer) valor).doubleValue();
                }
            }
        } else {
            if (tipo != null) {
                switch (tipo) {
                    case "int"     -> valor = 0;
                    case "float64" -> valor = 0.0;
                    case "string"  -> valor = "";
                    case "bool"    -> valor = false;
                    case "rune"    -> valor = (char) 0;
                    default        -> valor = null;
                }
            }
        }

        env.declare(node.name, tipo, valor);
        return null;
    }

    /**
     * Visita una asignacion de variable
     */
    @Override
    public Object visit(AssignNode node, Environment env) {
        if (!env.exists(node.name)) {
            registrarError("La variable '" + node.name
                + "' no ha sido declarada", node.line, node.column);
            return null;
        }

        Object valor = node.value.accept(this, env);
        String tipoVar = env.getType(node.name);
        String tipoVal = getTipo(valor);

        if (!sonCompatibles(tipoVar, tipoVal)) {
            registrarError("No se puede asignar un valor de tipo '"
                + tipoVal + "' a la variable '" + node.name
                + "' de tipo '" + tipoVar + "'",
                node.line, node.column);
            return null;
        }

        if (tipoVar.equals("float64") && valor instanceof Integer) {
            valor = ((Integer) valor).doubleValue();
        }

        env.setValue(node.name, valor);
        return null;
    }

    /**
     * Visita una asignacion compuesta (+=, -=)
     */
    @Override
    public Object visit(CompoundAssignNode node, Environment env) {
        if (!env.exists(node.name)) {
            registrarError("La variable '" + node.name
                + "' no ha sido declarada", node.line, node.column);
            return null;
        }

        Object valorActual = env.getValue(node.name);
        Object valorExpr = node.value.accept(this, env);
        String tipoVar = env.getType(node.name);
        Object resultado = null;

        if (node.operator.equals("+=")) {
            resultado = sumar(valorActual, valorExpr, node.line, node.column);
        } else if (node.operator.equals("-=")) {
            resultado = restar(valorActual, valorExpr, node.line, node.column);
        }

        if (resultado != null) {
            String tipoRes = getTipo(resultado);
            if (!sonCompatibles(tipoVar, tipoRes)) {
                registrarError("Operacion '" + node.operator
                    + "' produce tipo incompatible con '" + tipoVar + "'",
                    node.line, node.column);
                return null;
            }
            if (tipoVar.equals("float64") && resultado instanceof Integer) {
                resultado = ((Integer) resultado).doubleValue();
            }
            env.setValue(node.name, resultado);
        }
        return null;
    }

    /**
     * Visita un incremento o decremento (++, --)
     */
    @Override
    public Object visit(IncrDecrNode node, Environment env) {
        if (!env.exists(node.name)) {
            registrarError("La variable '" + node.name
                + "' no ha sido declarada", node.line, node.column);
            return null;
        }

        Object valor = env.getValue(node.name);

        if (valor instanceof Integer) {
            env.setValue(node.name,
                node.operator.equals("++") ? (Integer)valor + 1 : (Integer)valor - 1);
        } else if (valor instanceof Double) {
            env.setValue(node.name,
                node.operator.equals("++") ? (Double)valor + 1.0 : (Double)valor - 1.0);
        } else {
            registrarError("El operador '" + node.operator
                + "' solo aplica a tipos numericos", node.line, node.column);
        }
        return null;
    }

    /**
     * Visita una operacion binaria
     */
    @Override
    public Object visit(BinaryOpNode node, Environment env) {
        Object left = node.left.accept(this, env);
        Object right = node.right.accept(this, env);

        return switch (node.operator) {
            case "+"  -> sumar(left, right, node.line, node.column);
            case "-"  -> restar(left, right, node.line, node.column);
            case "*"  -> multiplicar(left, right, node.line, node.column);
            case "/"  -> dividir(left, right, node.line, node.column);
            case "%"  -> modulo(left, right, node.line, node.column);
            case "==" -> igualdad(left, right, true, node.line, node.column);
            case "!=" -> igualdad(left, right, false, node.line, node.column);
            case "<"  -> relacional(left, right, "<", node.line, node.column);
            case "<=" -> relacional(left, right, "<=", node.line, node.column);
            case ">"  -> relacional(left, right, ">", node.line, node.column);
            case ">=" -> relacional(left, right, ">=", node.line, node.column);
            case "&&" -> logico(left, right, "&&", node.line, node.column);
            case "||" -> logico(left, right, "||", node.line, node.column);
            default   -> {
                registrarError("Operador desconocido: " + node.operator,
                    node.line, node.column);
                yield null;
            }
        };
    }

    /**
     * Visita una operacion unaria (- o !)
     */
    @Override
    public Object visit(UnaryOpNode node, Environment env) {
        Object val = node.operand.accept(this, env);

        if (node.operator.equals("-")) {
            if (val instanceof Integer) return -(Integer) val;
            if (val instanceof Double)  return -(Double) val;
            registrarError("La negacion unaria solo aplica a tipos numericos",
                node.line, node.column);
            return null;
        } else if (node.operator.equals("!")) {
            if (val instanceof Boolean) return !(Boolean) val;
            registrarError("El operador '!' solo aplica a tipo bool",
                node.line, node.column);
            return null;
        }
        return null;
    }

    /**
     * Visita un literal
     */
    @Override
    public Object visit(LiteralNode node, Environment env) {
        return node.value;
    }

    /**
     * Visita un identificador
     */
    @Override
    public Object visit(IdentifierNode node, Environment env) {
        if (!env.exists(node.name)) {
            registrarError("La variable '" + node.name
                + "' no ha sido declarada", node.line, node.column);
            return null;
        }
        return env.getValue(node.name);
    }

    /**
     * Visita una sentencia if-else
     */
    @Override
    public Object visit(IfNode node, Environment env) {
        Object condicion = node.condition.accept(this, env);

        if (!(condicion instanceof Boolean)) {
            registrarError("La condicion del if debe ser de tipo bool",
                node.line, node.column);
            return null;
        }

        Environment envIf = new Environment(env);

        if ((Boolean) condicion) {
            for (Node stmt : node.thenStmts) {
                stmt.accept(this, envIf);
            }
        } else if (node.elseStmts != null) {
            Environment envElse = new Environment(env);
            for (Node stmt : node.elseStmts) {
                stmt.accept(this, envElse);
            }
        }
        return null;
    }

    /**
     * Visita una sentencia for
     */
    @Override
    public Object visit(ForNode node, Environment env) {
        Environment envFor = new Environment(env);

        if (node.init != null) {
            node.init.accept(this, envFor);
        }

        nivelFor++;

        try {
            while (true) {
                Object condicion = node.condition.accept(this, envFor);

                if (!(condicion instanceof Boolean)) {
                    registrarError("La condicion del for debe ser de tipo bool",
                        node.line, node.column);
                    break;
                }

                if (!(Boolean) condicion) break;

                Environment envBody = new Environment(envFor);
                try {
                    for (Node stmt : node.body) {
                        stmt.accept(this, envBody);
                    }
                } catch (ContinueException e) {
                    // Continue: saltar al update
                }

                if (node.update != null) {
                    node.update.accept(this, envFor);
                }
            }
        } catch (BreakException e) {
            // Break: salir del bucle
        } finally {
            nivelFor--;
        }

        return null;
    }

    /**
     * Visita un bloque independiente
     */
    @Override
    public Object visit(BlockNode node, Environment env) {
        Environment envBloque = new Environment(env);
        for (Node stmt : node.statements) {
            stmt.accept(this, envBloque);
        }
        return null;
    }

    /**
     * Visita una sentencia break
     */
    @Override
    public Object visit(BreakNode node, Environment env) {
        if (nivelFor == 0) {
            registrarError("'break' usado fuera de un bucle for",
                node.line, node.column);
            return null;
        }
        throw new BreakException();
    }

    /**
     * Visita una sentencia continue
     */
    @Override
    public Object visit(ContinueNode node, Environment env) {
        if (nivelFor == 0) {
            registrarError("'continue' usado fuera de un bucle for",
                node.line, node.column);
            return null;
        }
        throw new ContinueException();
    }

    /**
     * Visita una sentencia return.
     * Lanza ReturnException para salir de la funcion actual.
     */
    @Override
    public Object visit(ReturnNode node, Environment env) {
        Object valor = null;
        if (node.value != null) {
            valor = node.value.accept(this, env);
        }
        throw new ReturnException(valor);
    }

    /**
     * Visita una sentencia switch-case.
     * Evalua la expresion y ejecuta el case que coincida.
     * El break es implicito al final de cada case.
     */
    @Override
    public Object visit(SwitchNode node, Environment env) {
        Object valorSwitch = node.expression.accept(this, env);

        for (CaseNode caso : node.cases) {
            Object valorCase = caso.expression.accept(this, env);

            boolean coincide = false;
            if (valorSwitch == null && valorCase == null) {
                coincide = true;
            } else if (valorSwitch != null && valorCase != null) {
                if ((valorSwitch instanceof Integer || valorSwitch instanceof Double)
                        && (valorCase instanceof Integer || valorCase instanceof Double)) {
                    double ds = valorSwitch instanceof Integer
                        ? ((Integer) valorSwitch).doubleValue() : (Double) valorSwitch;
                    double dc = valorCase instanceof Integer
                        ? ((Integer) valorCase).doubleValue() : (Double) valorCase;
                    coincide = ds == dc;
                } else {
                    coincide = valorSwitch.equals(valorCase);
                }
            }

            if (coincide) {
                Environment envCase = new Environment(env);
                for (Node stmt : caso.stmts) {
                    stmt.accept(this, envCase);
                }
                return null;
            }
        }

        if (node.defaultStmts != null) {
            Environment envDefault = new Environment(env);
            for (Node stmt : node.defaultStmts) {
                stmt.accept(this, envDefault);
            }
        }
        return null;
    }

    /**
     * Visita un CaseNode individual.
     * Se maneja desde SwitchNode, no directamente.
     */
    @Override
    public Object visit(CaseNode node, Environment env) {
        return null;
    }

    /**
     * Visita fmt.Println
     */
    @Override
    public Object visit(PrintlnNode node, Environment env) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < node.args.size(); i++) {
            Object val = node.args.get(i).accept(this, env);
            if (i > 0) sb.append(" ");
            sb.append(formatearValor(val));
        }

        String salida = sb.toString();
        System.out.println(salida);
        consola.append(salida).append("\n");
        return null;
    }

    /**
     * Formatea un valor para imprimirlo
     */
    private String formatearValor(Object val) {
        if (val == null)            return "nil";
        if (val instanceof Boolean) return val.toString();
        if (val instanceof Double) {
            double d = (Double) val;
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf(d);
            }
            return String.valueOf(d);
        }
        return val.toString();
    }

    /**
     * Visita strconv.Atoi
     */
    @Override
    public Object visit(AtoiNode node, Environment env) {
        Object val = node.expr.accept(this, env);

        if (!(val instanceof String)) {
            registrarError("strconv.Atoi requiere un argumento de tipo string",
                node.line, node.column);
            return null;
        }

        try {
            return Integer.parseInt((String) val);
        } catch (NumberFormatException e) {
            registrarError("strconv.Atoi: no se puede convertir '"
                + val + "' a int", node.line, node.column);
            return null;
        }
    }

    /**
     * Visita strconv.ParseFloat
     */
    @Override
    public Object visit(ParseFloatNode node, Environment env) {
        Object val = node.expr.accept(this, env);

        if (!(val instanceof String)) {
            registrarError("strconv.ParseFloat requiere un argumento de tipo string",
                node.line, node.column);
            return null;
        }

        try {
            return Double.parseDouble((String) val);
        } catch (NumberFormatException e) {
            registrarError("strconv.ParseFloat: no se puede convertir '"
                + val + "' a float64", node.line, node.column);
            return null;
        }
    }

    /**
     * Visita reflect.TypeOf
     */
    @Override
    public Object visit(TypeOfNode node, Environment env) {
        Object val = node.expr.accept(this, env);
        return getTipo(val);
    }

    /**
     * Visita una llamada a funcion de usuario
     */
    @Override
    public Object visit(FuncCallNode node, Environment env) {
        registrarError("Las funciones de usuario se implementan en Fase 2",
            node.line, node.column);
        return null;
    }

    /* ===== OPERACIONES ARITMETICAS ===== */

    private Object sumar(Object a, Object b, int linea, int columna) {
        if (a instanceof Integer && b instanceof Integer) {
            return (Integer)a + (Integer)b;
        }
        if ((a instanceof Integer || a instanceof Double)
                && (b instanceof Integer || b instanceof Double)) {
            return ((Number)toDouble(a)).doubleValue()
                 + ((Number)toDouble(b)).doubleValue();
        }
        if (a instanceof String && b instanceof String) {
            return (String)a + (String)b;
        }
        registrarError("Operacion '+' no valida entre tipos '"
            + getTipo(a) + "' y '" + getTipo(b) + "'", linea, columna);
        return null;
    }

    private Object restar(Object a, Object b, int linea, int columna) {
        if (a instanceof Integer && b instanceof Integer) {
            return (Integer)a - (Integer)b;
        }
        if ((a instanceof Integer || a instanceof Double)
                && (b instanceof Integer || b instanceof Double)) {
            return ((Number)toDouble(a)).doubleValue()
                 - ((Number)toDouble(b)).doubleValue();
        }
        registrarError("Operacion '-' no valida entre tipos '"
            + getTipo(a) + "' y '" + getTipo(b) + "'", linea, columna);
        return null;
    }

    private Object multiplicar(Object a, Object b, int linea, int columna) {
        if (a instanceof Integer && b instanceof Integer) {
            return (Integer)a * (Integer)b;
        }
        if ((a instanceof Integer || a instanceof Double)
                && (b instanceof Integer || b instanceof Double)) {
            return ((Number)toDouble(a)).doubleValue()
                 * ((Number)toDouble(b)).doubleValue();
        }
        registrarError("Operacion '*' no valida entre tipos '"
            + getTipo(a) + "' y '" + getTipo(b) + "'", linea, columna);
        return null;
    }

    private Object dividir(Object a, Object b, int linea, int columna) {
        if ((b instanceof Integer && (Integer)b == 0)
                || (b instanceof Double && (Double)b == 0.0)) {
            registrarError("Division por cero", linea, columna);
            return null;
        }
        if (a instanceof Integer && b instanceof Integer) {
            return (Integer)a / (Integer)b;
        }
        if ((a instanceof Integer || a instanceof Double)
                && (b instanceof Integer || b instanceof Double)) {
            return ((Number)toDouble(a)).doubleValue()
                 / ((Number)toDouble(b)).doubleValue();
        }
        registrarError("Operacion '/' no valida entre tipos '"
            + getTipo(a) + "' y '" + getTipo(b) + "'", linea, columna);
        return null;
    }

    private Object modulo(Object a, Object b, int linea, int columna) {
        if (b instanceof Integer && (Integer)b == 0) {
            registrarError("Division por cero en operacion modulo", linea, columna);
            return null;
        }
        if (a instanceof Integer && b instanceof Integer) {
            return (Integer)a % (Integer)b;
        }
        registrarError("Operacion '%' solo es valida entre tipos int",
            linea, columna);
        return null;
    }

    private Object igualdad(Object a, Object b, boolean esIgual,
                             int linea, int columna) {
        if (a == null || b == null) {
            return esIgual ? (a == b) : (a != b);
        }
        if ((a instanceof Integer || a instanceof Double)
                && (b instanceof Integer || b instanceof Double)) {
            double da = ((Number)toDouble(a)).doubleValue();
            double db = ((Number)toDouble(b)).doubleValue();
            return esIgual ? da == db : da != db;
        }
        if (a.getClass().equals(b.getClass())) {
            return esIgual ? a.equals(b) : !a.equals(b);
        }
        registrarError("No se puede comparar tipos '"
            + getTipo(a) + "' y '" + getTipo(b) + "'", linea, columna);
        return null;
    }

    private Object relacional(Object a, Object b, String op,
                               int linea, int columna) {
        if ((a instanceof Integer || a instanceof Double)
                && (b instanceof Integer || b instanceof Double)) {
            double da = ((Number)toDouble(a)).doubleValue();
            double db = ((Number)toDouble(b)).doubleValue();
            return switch (op) {
                case "<"  -> da < db;
                case "<=" -> da <= db;
                case ">"  -> da > db;
                case ">=" -> da >= db;
                default   -> null;
            };
        }
        if (a instanceof Character && b instanceof Character) {
            int ca = (Character) a;
            int cb = (Character) b;
            return switch (op) {
                case "<"  -> ca < cb;
                case "<=" -> ca <= cb;
                case ">"  -> ca > cb;
                case ">=" -> ca >= cb;
                default   -> null;
            };
        }
        registrarError("Operacion '" + op + "' no valida entre tipos '"
            + getTipo(a) + "' y '" + getTipo(b) + "'", linea, columna);
        return null;
    }

    private Object logico(Object a, Object b, String op,
                           int linea, int columna) {
        if (!(a instanceof Boolean) || !(b instanceof Boolean)) {
            registrarError("Los operadores logicos solo aplican a tipo bool",
                linea, columna);
            return null;
        }
        if (op.equals("&&")) return (Boolean)a && (Boolean)b;
        if (op.equals("||")) return (Boolean)a || (Boolean)b;
        return null;
    }
}