package analisis;

import java_cup.runtime.Symbol;

%%

%cup
%class Scanner
%public
%line
%column
%unicode

%{
    public static java.util.ArrayList<String[]> listaTokens = new java.util.ArrayList<>();

    private Symbol token(int tipo) {
        String[] tok = {
            yytext(),
            sym.terminalNames[tipo],
            String.valueOf(yyline),
            String.valueOf(yycolumn)
        };
        listaTokens.add(tok);
        return new Symbol(tipo, yyline, yycolumn, yytext());
    }

    private Symbol token(int tipo, Object valor) {
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
    yyline = 1;
    yycolumn = 1;
%init}

Blancos         = [ \t\r\f\n]+
Identificador   = [a-zA-Z_][a-zA-Z0-9_]*
Entero          = [0-9]+
Decimal         = [0-9]+"."[0-9]+
Cadena          = \"([^\"\\]|\\.)*\"
Rune            = \'([^\'\\]|\\.)\'
ComentarioLinea = "//"[^\r\n]*
ComentarioBloque = "/*"[^*]*\*+([^/*][^*]*\*+)*"/"

%%

{ComentarioLinea}   { /* ignorar */ }
{ComentarioBloque}  { /* ignorar */ }

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
"int"           { return token(sym.TINT); }
"float64"       { return token(sym.TFLOAT64); }
"string"        { return token(sym.TSTRING); }
"bool"          { return token(sym.TBOOL); }
"rune"          { return token(sym.TRUNE); }

"fmt.Println"           { return token(sym.PRINTLN); }
"strconv.Atoi"          { return token(sym.ATOI); }
"strconv.ParseFloat"    { return token(sym.PARSEFLOAT); }
"reflect.TypeOf"        { return token(sym.TYPEOF); }
"len"                   { return token(sym.LEN); }
"append"                { return token(sym.APPEND); }
"slices.Index"          { return token(sym.SLICEINDEX); }

":="            { return token(sym.ASIGNDECL); }
"+="            { return token(sym.MASIGUAL); }
"-="            { return token(sym.MENOSIGUAL); }
"="             { return token(sym.ASIGN); }

"=="            { return token(sym.IGUAL); }
"!="            { return token(sym.DIFERENTE); }
"<="            { return token(sym.MENORIGUAL); }
">="            { return token(sym.MAYORIGUAL); }
"<"             { return token(sym.MENOR); }
">"             { return token(sym.MAYOR); }

"&&"            { return token(sym.AND); }
"||"            { return token(sym.OR); }
"!"             { return token(sym.NOT); }

"++"            { return token(sym.INC); }
"--"            { return token(sym.DEC); }
"+"             { return token(sym.MAS); }
"-"             { return token(sym.MENOS); }
"*"             { return token(sym.MULT); }
"/"             { return token(sym.DIV); }
"%"             { return token(sym.MOD); }

"("             { return token(sym.PAR1); }
")"             { return token(sym.PAR2); }
"{"             { return token(sym.LLAVE1); }
"}"             { return token(sym.LLAVE2); }
"["             { return token(sym.CORCHETE1); }
"]"             { return token(sym.CORCHETE2); }
","             { return token(sym.COMA); }
";"             { return token(sym.PTCOMA); }
"."             { return token(sym.PUNTO); }

"switch"        { return token(sym.SWITCH); }
"case"          { return token(sym.CASE); }
"default"       { return token(sym.DEFAULT); }
":"             { return token(sym.COLON); }

{Decimal}       { return token(sym.DECIMAL, Double.parseDouble(yytext())); }
{Entero}        { return token(sym.ENTERO, Integer.parseInt(yytext())); }
{Cadena}        {
    String s = yytext().substring(1, yytext().length()-1);
    s = s.replace("\\n", "\n")
         .replace("\\t", "\t")
         .replace("\\r", "\r")
         .replace("\\\"", "\"")
         .replace("\\\\", "\\");
    return token(sym.CADENA, s);
}
{Rune}          {
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

{Identificador} { return token(sym.ID, yytext()); }
{Blancos}       { /* ignorar */ }

[^]             {
    System.err.println("[ERROR LEXICO] Caracter no reconocido: '"
        + yytext() + "' en linea " + yyline + ", columna " + yycolumn);
}