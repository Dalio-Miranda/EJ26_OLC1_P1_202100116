/*
 * Analizador Léxico para el lenguaje GoLite
 * Generado con JFlex 1.9.1
 * 
 * Este archivo define todos los tokens del lenguaje GoLite,
 * incluyendo palabras reservadas, operadores, literales,
 * identificadores y comentarios.
 * 
 * @author Dalio Miranda - 202100116
 * @course Organización de Lenguajes y Compiladores 1
 */

package analisis;

import java_cup.runtime.Symbol;

%%

/* ===== CONFIGURACION DE JFLEX ===== */
%cup                    /* Indica que se usara con CUP */
%class Scanner          /* Nombre de la clase generada */
%public                 /* La clase sera publica */
%line                   /* Activa conteo de lineas */
%column                 /* Activa conteo de columnas */
%8bit                   /* Caracteres en formato UTF-8 */

/* ===== CODIGO DE USUARIO ===== */
%{
    /* Lista para almacenar los tokens reconocidos para el reporte */
    public static java.util.ArrayList<String[]> listaTokens = new java.util.ArrayList<>();

    /**
     * Crea un Symbol y lo registra en la lista de tokens
     * @param tipo Tipo del token segun sym
     * @return Symbol con la informacion del token
     */
    private Symbol token(int tipo) {
        /* Registrar token en la lista para el reporte */
        String[] tok = {
            yytext(),
            sym.terminalNames[tipo],
            String.valueOf(yyline),
            String.valueOf(yycolumn)
        };
        listaTokens.add(tok);
        return new Symbol(tipo, yyline, yycolumn, yytext());
    }

    /**
     * Crea un Symbol con valor y lo registra en la lista de tokens
     * @param tipo Tipo del token segun sym
     * @param valor Valor semantico del token
     * @return Symbol con la informacion del token
     */
    private Symbol token(int tipo, Object valor) {
        /* Registrar token en la lista para el reporte */
        String[] tok = {
            yytext(),
            sym.terminalNames[tipo],
            String.valueOf(yyline),
            String.valueOf(yycolumn)
        };
        listaTokens.add(tok);
        return new Symbol(tipo, yyline, yycolumn, valor);
    }
%}

%init{
    /* Inicializar contadores en 1 */
    yyline = 1;
    yycolumn = 1;
%init}

/* ===== DEFINICIONES DE PATRONES ===== */

/* Espacios en blanco y saltos de linea */
Blancos         = [ \t\r\f\n]+

/* Identificador: inicia con letra o guion bajo, seguido de letras, digitos o guion bajo */
Identificador   = [a-zA-Z_][a-zA-Z0-9_]*

/* Literales numericos */
Entero          = [0-9]+
Decimal         = [0-9]+"."[0-9]+

/* Literal de cadena: texto entre comillas dobles con soporte de secuencias de escape */
Cadena          = \"([^\"\\]|\\.)*\"

/* Literal de rune: caracter entre comillas simples con soporte de secuencias de escape */
Rune            = \'([^\'\\]|\\.)\'

/* Comentarios de una linea: desde // hasta fin de linea */
ComentarioLinea = "//"[^\r\n]*

/* Comentarios multilinea: desde /* hasta */
ComentarioBloque = "/*"[^*]*\*+([^/*][^*]*\*+)*"/"

%%

/* ===== REGLAS LEXICAS ===== */

/* --- Comentarios (se ignoran, no generan tokens) --- */
{ComentarioLinea}   { /* ignorar comentarios de linea */ }
{ComentarioBloque}  { /* ignorar comentarios de bloque */ }

/* --- Palabras Reservadas --- */
/* Estas deben ir ANTES que la regla de identificadores */
"var"           { return token(sym.VAR); }
"func"          { return token(sym.FUNC); }
"if"            { return token(sym.IF); }
"else"          { return token(sym.ELSE); }
"for"           { return token(sym.FOR); }
"break"         { return token(sym.BREAK); }
"continue"      { return token(sym.CONTINUE); }
"return"        { return token(sym.RETURN); }
"nil"           { return token(sym.NIL); }
"true"          { return token(sym.TRUE, true); }
"false"         { return token(sym.FALSE, false); }

/* --- Tipos de datos primitivos --- */
"int"           { return token(sym.TINT); }
"float64"       { return token(sym.TFLOAT64); }
"string"        { return token(sym.TSTRING); }
"bool"          { return token(sym.TBOOL); }
"rune"          { return token(sym.TRUNE); }

/* --- Funciones embebidas --- */
/* Se reconocen como tokens especiales para facilitar el parsing */
"fmt.Println"           { return token(sym.PRINTLN); }
"strconv.Atoi"          { return token(sym.ATOI); }
"strconv.ParseFloat"    { return token(sym.PARSEFLOAT); }
"reflect.TypeOf"        { return token(sym.TYPEOF); }

/* --- Operadores de asignacion --- */
/* Deben ir antes que los operadores simples para evitar ambiguedad */
":="            { return token(sym.ASIGNDECL); }
"+="            { return token(sym.MASIGUAL); }
"-="            { return token(sym.MENOSIGUAL); }
"="             { return token(sym.ASIGN); }

/* --- Operadores de comparacion --- */
"=="            { return token(sym.IGUAL); }
"!="            { return token(sym.DIFERENTE); }
"<="            { return token(sym.MENORIGUAL); }
">="            { return token(sym.MAYORIGUAL); }
"<"             { return token(sym.MENOR); }
">"             { return token(sym.MAYOR); }

/* --- Operadores logicos --- */
"&&"            { return token(sym.AND); }
"||"            { return token(sym.OR); }
"!"             { return token(sym.NOT); }

/* --- Operadores aritmeticos --- */
"++"            { return token(sym.INC); }
"--"            { return token(sym.DEC); }
"+"             { return token(sym.MAS); }
"-"             { return token(sym.MENOS); }
"*"             { return token(sym.MULT); }
"/"             { return token(sym.DIV); }
"%"             { return token(sym.MOD); }

/* --- Delimitadores y signos de agrupacion --- */
"("             { return token(sym.PAR1); }
")"             { return token(sym.PAR2); }
"{"             { return token(sym.LLAVE1); }
"}"             { return token(sym.LLAVE2); }
"["             { return token(sym.CORCHETE1); }
"]"             { return token(sym.CORCHETE2); }
","             { return token(sym.COMA); }
";"             { return token(sym.PTCOMA); }
"."             { return token(sym.PUNTO); }

/* --- Literales --- */
{Decimal}       { return token(sym.DECIMAL, Double.parseDouble(yytext())); }
{Entero}        { return token(sym.ENTERO, Integer.parseInt(yytext())); }
{Cadena}        { 
    /* Eliminar comillas y procesar secuencias de escape */
    String s = yytext().substring(1, yytext().length()-1);
    s = s.replace("\\n", "\n")
         .replace("\\t", "\t")
         .replace("\\r", "\r")
         .replace("\\\"", "\"")
         .replace("\\\\", "\\");
    return token(sym.CADENA, s); 
}
{Rune}          { 
    /* Procesar literal de rune obteniendo el caracter */
    String s = yytext().substring(1, yytext().length()-1);
    char c;
    if(s.equals("\\n"))       c = '\n';
    else if(s.equals("\\t"))  c = '\t';
    else if(s.equals("\\r"))  c = '\r';
    else if(s.equals("\\'"))  c = '\'';
    else if(s.equals("\\\\")) c = '\\';
    else c = s.charAt(0);
    return token(sym.RUNELIT, c); 
}

/* --- Identificadores --- */
/* Va despues de palabras reservadas para que estas tengan prioridad */
{Identificador} { return token(sym.ID, yytext()); }

/* --- Espacios en blanco (se ignoran) --- */
{Blancos}       { /* ignorar espacios y saltos de linea */ }

/* --- Error lexico --- */
/* Cualquier caracter no reconocido genera un error lexico */
[^]             { 
    System.err.println("[ERROR LEXICO] Caracter no reconocido: '" 
        + yytext() + "' en linea " + yyline + ", columna " + yycolumn);
}