package com.golite.interpreter;

import com.golite.ast.*;
import java.util.ArrayList;

/**
 * Intérprete del lenguaje GoLite.
 * Implementa el patron Visitor para recorrer el AST
 * y ejecutar cada nodo segun su tipo.
 * 
 * Maneja:
 * - Declaracion y asignacion de variables con tipos estaticos
 * - Operaciones aritmeticas con conversion implicita int->float64
 * - Operaciones de comparacion y logicas
 * - Sentencias if/else, for, break, continue
 * - Funciones embebidas: fmt.Println, strconv.Atoi, 
 *   strconv.ParseFloat, reflect.TypeOf
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */
public class Interpreter implements Visitor {

    /* ===== EXCEPCIONES DE CONTROL DE FLUJO ===== */

    /**
     * Excepcion interna para manejar el break.
     * Se lanza cuando se encuentra un break y es
     * capturada por el nodo ForNode.
     */
    private static class BreakException extends RuntimeException {
        public BreakException() { super(); }
    }

    /**
     * Excepcion interna para manejar el continue.
     * Se lanza cuando se encuentra un continue y es
     * capturada por el nodo ForNode.
     */
    private static class ContinueException extends RuntimeException {
        public ContinueException() { super(); }
    }

    /* ===== ESTADO DEL INTERPRETE ===== */

    // Lista de errores semanticos encontrados durante la ejecucion
    public ArrayList<String[]> errores = new ArrayList<>();

    // Buffer de salida para la consola de la GUI
    public StringBuilder consola = new StringBuilder();

    // Indica si actualmente estamos dentro de un bucle for
    // (para validar break y continue)
    private int nivelFor = 0;

    /* ===== METODOS DE UTILIDAD ===== */

    /**
     * Registra un error semantico en la lista de errores
     * @param descripcion Descripcion del error
     * @param linea Linea donde ocurrio
     * @param columna Columna donde ocurrio
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
     * Obtiene el tipo de dato de un valor Java
     * @param valor Valor del que se quiere obtener el tipo
     * @return String con el tipo GoLite del valor
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
     * Verifica si dos tipos son compatibles para asignacion.
     * Permite conversion implicita de int a float64.
     * @param tipoVar Tipo de la variable
     * @param tipoVal Tipo del valor a asignar
     * @return true si son compatibles
     */
    private boolean sonCompatibles(String tipoVar, String tipoVal) {
        if (tipoVar.equals(tipoVal)) return true;
        // Conversion implicita: int puede asignarse a float64
        if (tipoVar.equals("float64") && tipoVal.equals("int")) return true;
        return false;
    }

    /**
     * Convierte un valor a Double si es Integer,
     * para operaciones aritmeticas mixtas int/float64
     * @param valor Valor a convertir
     * @return Double si era Integer, el mismo valor si ya era Double
     */
    private Object toDouble(Object valor) {
        if (valor instanceof Integer) return ((Integer) valor).doubleValue();
        return valor;
    }

    /* ===== IMPLEMENTACION DEL VISITOR ===== */

    /**
     * Visita el nodo raiz del programa.
     * Ejecuta todas las sentencias en el ambiente global.
     */
    @Override
    public Object visit(ProgramNode node, Environment env) {
        for (Node stmt : node.statements) {
            stmt.accept(this, env);
        }
        return null;
    }

    /**
     * Visita una declaracion de variable.
     * Maneja var x tipo = expr, var x tipo y x := expr
     */
    @Override
    public Object visit(VarDeclNode node, Environment env) {
        // Verificar que no exista en el mismo ambito
        if (env.existsLocal(node.name)) {
            registrarError("La variable '" + node.name 
                + "' ya fue declarada en este ambito", node.line, node.column);
            return null;
        }

        String tipo = node.type;
        Object valor = null;

        // Evaluar el valor inicial si existe
        if (node.value != null) {
            valor = node.value.accept(this, env);
            String tipoValor = getTipo(valor);

            if (node.isImplicit) {
                // Declaracion implicita: inferir tipo del valor
                tipo = tipoValor;
            } else {
                // Declaracion explicita: verificar compatibilidad de tipos
                if (!sonCompatibles(tipo, tipoValor)) {
                    registrarError("No se puede asignar un valor de tipo '" 
                        + tipoValor + "' a la variable de tipo '" + tipo + "'",
                        node.line, node.column);
                    return null;
                }
                // Conversion implicita int -> float64
                if (tipo.equals("float64") && valor instanceof Integer) {
                    valor = ((Integer) valor).doubleValue();
                }
            }
        } else {
            // Sin valor inicial: asignar valor por defecto segun tipo
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

        // Declarar la variable en el ambiente actual
        env.declare(node.name, tipo, valor);
        return null;
    }

    /**
     * Visita una asignacion de variable.
     * Verifica que la variable exista y que el tipo sea compatible.
     */
    @Override
    public Object visit(AssignNode node, Environment env) {
        // Verificar que la variable exista
        if (!env.exists(node.name)) {
            registrarError("La variable '" + node.name 
                + "' no ha sido declarada", node.line, node.column);
            return null;
        }

        // Evaluar el nuevo valor
        Object valor = node.value.accept(this, env);
        String tipoVar = env.getType(node.name);
        String tipoVal = getTipo(valor);

        // Verificar compatibilidad de tipos
        if (!sonCompatibles(tipoVar, tipoVal)) {
            registrarError("No se puede asignar un valor de tipo '" 
                + tipoVal + "' a la variable '" + node.name 
                + "' de tipo '" + tipoVar + "'",
                node.line, node.column);
            return null;
        }

        // Conversion implicita int -> float64
        if (tipoVar.equals("float64") && valor instanceof Integer) {
            valor = ((Integer) valor).doubleValue();
        }

        env.setValue(node.name, valor);
        return null;
    }

    /**
     * Visita una asignacion compuesta (+=, -=).
     * Equivale a variable = variable operador expresion.
     */
    @Override
    public Object visit(CompoundAssignNode node, Environment env) {
        // Verificar que la variable exista
        if (!env.exists(node.name)) {
            registrarError("La variable '" + node.name 
                + "' no ha sido declarada", node.line, node.column);
            return null;
        }

        Object valorActual = env.getValue(node.name);
        Object valorExpr = node.value.accept(this, env);
        String tipoVar = env.getType(node.name);
        Object resultado = null;

        // Realizar la operacion segun el operador
        if (node.operator.equals("+=")) {
            resultado = sumar(valorActual, valorExpr, node.line, node.column);
        } else if (node.operator.equals("-=")) {
            resultado = restar(valorActual, valorExpr, node.line, node.column);
        }

        if (resultado != null) {
            // Verificar compatibilidad del resultado con el tipo de la variable
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
     * Visita un incremento o decremento (++, --).
     * Solo aplica a variables numericas.
     */
    @Override
    public Object visit(IncrDecrNode node, Environment env) {
        // Verificar que la variable exista
        if (!env.exists(node.name)) {
            registrarError("La variable '" + node.name 
                + "' no ha sido declarada", node.line, node.column);
            return null;
        }

        Object valor = env.getValue(node.name);

        // Aplicar incremento o decremento segun el tipo
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
     * Visita una operacion binaria.
     * Maneja aritmetica, comparacion y logica con sus reglas de tipos.
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
     * Visita una operacion unaria (- o !).
     */
    @Override
    public Object visit(UnaryOpNode node, Environment env) {
        Object val = node.operand.accept(this, env);

        if (node.operator.equals("-")) {
            // Negacion aritmetica
            if (val instanceof Integer) return -(Integer) val;
            if (val instanceof Double)  return -(Double) val;
            registrarError("La negacion unaria solo aplica a tipos numericos",
                node.line, node.column);
            return null;
        } else if (node.operator.equals("!")) {
            // Negacion logica
            if (val instanceof Boolean) return !(Boolean) val;
            registrarError("El operador '!' solo aplica a tipo bool",
                node.line, node.column);
            return null;
        }
        return null;
    }

    /**
     * Visita un literal y retorna su valor directamente.
     */
    @Override
    public Object visit(LiteralNode node, Environment env) {
        return node.value;
    }

    /**
     * Visita un identificador buscando su valor en el ambiente.
     */
    @Override
    public Object visit(IdentifierNode node, Environment env) {
        if (!env.exists(node.name)) {
            registrarError("La variable '" + node.name 
                + "' no ha sido declarada", node.line, node.column);
            return null;
        }
        Object valor = env.getValue(node.name);
        if (valor == null && env.getType(node.name) != null
                && !env.getType(node.name).equals("nil")) {
            registrarError("La variable '" + node.name 
                + "' tiene valor nil", node.line, node.column);
        }
        return valor;
    }

    /**
     * Visita una sentencia if-else.
     * Evalua la condicion y ejecuta el bloque correspondiente.
     */
    @Override
    public Object visit(IfNode node, Environment env) {
        Object condicion = node.condition.accept(this, env);

        // Verificar que la condicion sea de tipo bool
        if (!(condicion instanceof Boolean)) {
            registrarError("La condicion del if debe ser de tipo bool",
                node.line, node.column);
            return null;
        }

        // Crear nuevo ambiente para el bloque
        Environment envIf = new Environment(env);

        if ((Boolean) condicion) {
            // Ejecutar bloque if
            for (Node stmt : node.thenStmts) {
                stmt.accept(this, envIf);
            }
        } else if (node.elseStmts != null) {
            // Ejecutar bloque else
            Environment envElse = new Environment(env);
            for (Node stmt : node.elseStmts) {
                stmt.accept(this, envElse);
            }
        }
        return null;
    }

    /**
     * Visita una sentencia for.
     * Soporta for condicion { } y for init; cond; update { }
     */
    @Override
    public Object visit(ForNode node, Environment env) {
        // Crear ambiente para el for
        Environment envFor = new Environment(env);

        // Ejecutar inicializacion si existe
        if (node.init != null) {
            node.init.accept(this, envFor);
        }

        nivelFor++; // Entrar al bucle

        try {
            while (true) {
                // Evaluar condicion
                Object condicion = node.condition.accept(this, envFor);

                if (!(condicion instanceof Boolean)) {
                    registrarError("La condicion del for debe ser de tipo bool",
                        node.line, node.column);
                    break;
                }

                if (!(Boolean) condicion) break;

                // Ejecutar cuerpo del bucle
                Environment envBody = new Environment(envFor);
                try {
                    for (Node stmt : node.body) {
                        stmt.accept(this, envBody);
                    }
                } catch (ContinueException e) {
                    // Continue: saltar al update y siguiente iteracion
                }

                // Ejecutar actualizacion si existe
                if (node.update != null) {
                    node.update.accept(this, envFor);
                }
            }
        } catch (BreakException e) {
            // Break: salir del bucle
        } finally {
            nivelFor--; // Salir del bucle
        }

        return null;
    }

    /**
     * Visita un bloque independiente.
     * Crea un nuevo ambiente local para las variables del bloque.
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
     * Visita una sentencia break.
     * Lanza excepcion interna para salir del bucle actual.
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
     * Visita una sentencia continue.
     * Lanza excepcion interna para saltar a la siguiente iteracion.
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
     * Visita fmt.Println.
     * Imprime todos los argumentos separados por espacio
     * y agrega salto de linea al final.
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
     * Formatea un valor para imprimirlo con fmt.Println.
     * @param val Valor a formatear
     * @return String con la representacion del valor
     */
    private String formatearValor(Object val) {
        if (val == null)            return "nil";
        if (val instanceof Boolean) return val.toString();
        if (val instanceof Double) {
            // Imprimir sin notacion cientifica
            double d = (Double) val;
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf(d);
            }
            return String.valueOf(d);
        }
        return val.toString();
    }

    /**
     * Visita strconv.Atoi.
     * Convierte un string a int.
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
     * Visita strconv.ParseFloat.
     * Convierte un string a float64.
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
     * Visita reflect.TypeOf.
     * Retorna el tipo GoLite de la expresion como string.
     */
    @Override
    public Object visit(TypeOfNode node, Environment env) {
        Object val = node.expr.accept(this, env);
        return getTipo(val);
    }

    /**
     * Visita una llamada a funcion de usuario.
     * Por ahora registra error ya que las funciones
     * son parte de la Fase 2.
     */
    @Override
    public Object visit(FuncCallNode node, Environment env) {
        registrarError("Las funciones de usuario no estan implementadas en Fase 1",
            node.line, node.column);
        return null;
    }

    /* ===== OPERACIONES ARITMETICAS ===== */

    /**
     * Realiza la suma de dos valores.
     * Soporta int+int, int+float64, float64+float64, string+string.
     */
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

    /**
     * Realiza la resta de dos valores.
     * Soporta int-int, int-float64, float64-float64.
     */
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

    /**
     * Realiza la multiplicacion de dos valores.
     * Soporta int*int, int*float64, float64*float64.
     */
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

    /**
     * Realiza la division de dos valores.
     * Verifica division por cero.
     * int/int trunca el resultado.
     */
    private Object dividir(Object a, Object b, int linea, int columna) {
        // Verificar division por cero
        if ((b instanceof Integer && (Integer)b == 0) 
                || (b instanceof Double && (Double)b == 0.0)) {
            registrarError("Division por cero", linea, columna);
            return null;
        }
        if (a instanceof Integer && b instanceof Integer) {
            return (Integer)a / (Integer)b; // Division entera (trunca)
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

    /**
     * Realiza el modulo de dos valores enteros.
     * Solo soporta int%int.
     */
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

    /**
     * Realiza comparacion de igualdad o desigualdad.
     * Soporta comparacion entre tipos compatibles.
     */
    private Object igualdad(Object a, Object b, boolean esIgual, 
                             int linea, int columna) {
        if (a == null || b == null) {
            return esIgual ? (a == b) : (a != b);
        }
        // Comparacion entre numericos (permite int == float64)
        if ((a instanceof Integer || a instanceof Double) 
                && (b instanceof Integer || b instanceof Double)) {
            double da = ((Number)toDouble(a)).doubleValue();
            double db = ((Number)toDouble(b)).doubleValue();
            return esIgual ? da == db : da != db;
        }
        // Comparacion del mismo tipo
        if (a.getClass().equals(b.getClass())) {
            return esIgual ? a.equals(b) : !a.equals(b);
        }
        registrarError("No se puede comparar tipos '" 
            + getTipo(a) + "' y '" + getTipo(b) + "'", linea, columna);
        return null;
    }

    /**
     * Realiza operaciones relacionales (<, <=, >, >=).
     * Soporta numericos, strings y runes.
     */
    private Object relacional(Object a, Object b, String op, 
                               int linea, int columna) {
        // Comparacion numerica
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
        // Comparacion de runes (por valor ASCII)
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

    /**
     * Realiza operaciones logicas (&& y ||).
     * Ambos operandos deben ser de tipo bool.
     */
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