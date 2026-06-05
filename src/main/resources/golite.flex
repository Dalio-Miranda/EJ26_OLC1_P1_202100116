package com.golite.lexer;

import com.github.vbmacher.cup.runtime.Symbol;
import com.golite.parser.sym;
import java.util.ArrayList;

%%

%class Lexer
%unicode
%cup
%line
%column
%public

%{
    public static ArrayList<String[]> tokens = new ArrayList<>();

    private Symbol symbol(int type) {
        String[] token = {yytext(), sym.terminalNames[type], String.valueOf(yyline+1), String.valueOf(yycolumn+1)};
        tokens.add(token);
        return new Symbol(type, yyline+1, yycolumn+1, yytext());
    }

    private Symbol symbol(int type, Object value) {
        String[] token = {yytext(), sym.terminalNames[type], String.valueOf(yyline+1), String.valueOf(yycolumn+1)};
        tokens.add(token);
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
%}

/* Definiciones */
LineTerminator  = \r|\n|\r\n
WhiteSpace      = {LineTerminator} | [ \t\f]
Identifier      = [a-zA-Z_][a-zA-Z0-9_]*
IntLiteral      = [0-9]+
FloatLiteral    = [0-9]+"."[0-9]+
StringLiteral   = \"([^\"\\]|\\.)*\"
RuneLiteral     = \'([^\'\\]|\\.)\'
Comment         = "//"[^\r\n]*
MultiComment    = "/*"[^*]*\*+([^/*][^*]*\*+)*"/"

%%

/* Palabras reservadas */
"var"           { return symbol(sym.VAR); }
"func"          { return symbol(sym.FUNC); }
"if"            { return symbol(sym.IF); }
"else"          { return symbol(sym.ELSE); }
"for"           { return symbol(sym.FOR); }
"break"         { return symbol(sym.BREAK); }
"continue"      { return symbol(sym.CONTINUE); }
"return"        { return symbol(sym.RETURN); }
"nil"           { return symbol(sym.NIL); }
"true"          { return symbol(sym.TRUE, true); }
"false"         { return symbol(sym.FALSE, false); }
"int"           { return symbol(sym.TINT); }
"float64"       { return symbol(sym.TFLOAT64); }
"string"        { return symbol(sym.TSTRING); }
"bool"          { return symbol(sym.TBOOL); }
"rune"          { return symbol(sym.TRUNE); }

/* Funciones embebidas */
"fmt.Println"       { return symbol(sym.PRINTLN); }
"strconv.Atoi"      { return symbol(sym.ATOI); }
"strconv.ParseFloat"{ return symbol(sym.PARSEFLOAT); }
"reflect.TypeOf"    { return symbol(sym.TYPEOF); }

/* Operadores aritméticos */
"+"             { return symbol(sym.PLUS); }
"-"             { return symbol(sym.MINUS); }
"*"             { return symbol(sym.TIMES); }
"/"             { return symbol(sym.DIVIDE); }
"%"             { return symbol(sym.MOD); }

/* Operadores de asignación */
":="            { return symbol(sym.ASSIGN_DECL); }
"="             { return symbol(sym.ASSIGN); }
"+="            { return symbol(sym.PLUS_ASSIGN); }
"-="            { return symbol(sym.MINUS_ASSIGN); }

/* Operadores de comparación */
"=="            { return symbol(sym.EQ); }
"!="            { return symbol(sym.NEQ); }
"<"             { return symbol(sym.LT); }
"<="            { return symbol(sym.LE); }
">"             { return symbol(sym.GT); }
">="            { return symbol(sym.GE); }

/* Operadores lógicos */
"&&"            { return symbol(sym.AND); }
"||"            { return symbol(sym.OR); }
"!"             { return symbol(sym.NOT); }

/* Incremento / Decremento */
"++"            { return symbol(sym.INC); }
"--"            { return symbol(sym.DEC); }

/* Delimitadores */
"("             { return symbol(sym.LPAREN); }
")"             { return symbol(sym.RPAREN); }
"{"             { return symbol(sym.LBRACE); }
"}"             { return symbol(sym.RBRACE); }
"["             { return symbol(sym.LBRACKET); }
"]"             { return symbol(sym.RBRACKET); }
","             { return symbol(sym.COMMA); }
";"             { return symbol(sym.SEMICOLON); }
"."             { return symbol(sym.DOT); }

/* Literales */
{IntLiteral}    { return symbol(sym.INT_LITERAL, Integer.parseInt(yytext())); }
{FloatLiteral}  { return symbol(sym.FLOAT_LITERAL, Double.parseDouble(yytext())); }
{StringLiteral} { 
    String s = yytext();
    s = s.substring(1, s.length()-1);
    s = s.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r")
         .replace("\\\"", "\"").replace("\\\\", "\\");
    return symbol(sym.STRING_LITERAL, s); 
}
{RuneLiteral}   { 
    String s = yytext();
    s = s.substring(1, s.length()-1);
    char c;
    if(s.equals("\\n")) c = '\n';
    else if(s.equals("\\t")) c = '\t';
    else if(s.equals("\\r")) c = '\r';
    else if(s.equals("\\'")) c = '\'';
    else if(s.equals("\\\\")) c = '\\';
    else c = s.charAt(0);
    return symbol(sym.RUNE_LITERAL, c); 
}

/* Identificadores */
{Identifier}    { return symbol(sym.ID, yytext()); }

/* Comentarios y espacios */
{Comment}       { /* ignorar */ }
{MultiComment}  { /* ignorar */ }
{WhiteSpace}    { /* ignorar */ }

/* Error léxico */
[^]             { 
    System.err.println("Error léxico: caracter no reconocido '" + yytext() + "' en línea " + (yyline+1) + ", columna " + (yycolumn+1));
}