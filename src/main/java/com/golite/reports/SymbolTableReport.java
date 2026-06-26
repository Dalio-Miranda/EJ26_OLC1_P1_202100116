package com.golite.reports;

import com.golite.ast.*;
import java.util.ArrayList;

public class SymbolTableReport {

    private ArrayList<String[]> simbolos = new ArrayList<>();

    public ArrayList<String[]> generar(ProgramNode ast) {
        simbolos.clear();
        recorrerLista(ast.statements, "Global");
        return simbolos;
    }

    private void recorrerLista(ArrayList<Node> lista, String ambito) {
        if (lista == null) return;

        for (Node nodo : lista) {
            recorrer(nodo, ambito);
        }
    }

    private void recorrer(Node nodo, String ambito) {
        if (nodo == null) return;

        if (nodo instanceof FuncDeclNode f) {
            simbolos.add(new String[]{
                f.name,
                f.returnType == null ? "void" : f.returnType,
                "Funcion",
                "Global",
                String.valueOf(f.line),
                String.valueOf(f.column)
            });

            for (String[] param : f.params) {
                simbolos.add(new String[]{
                    param[0],
                    param[1],
                    "Parametro",
                    f.name,
                    String.valueOf(f.line),
                    String.valueOf(f.column)
                });
            }

            recorrerLista(f.body, f.name);
        }

        else if (nodo instanceof VarDeclNode v) {
            simbolos.add(new String[]{
                v.name,
                v.type == null ? "inferido" : v.type,
                "Variable",
                ambito,
                String.valueOf(v.line),
                String.valueOf(v.column)
            });
        }

        else if (nodo instanceof BlockNode b) {
            recorrerLista(b.statements, ambito + "_bloque");
        }

        else if (nodo instanceof IfNode i) {
            recorrerLista(i.thenStmts, ambito + "_if");
            recorrerLista(i.elseStmts, ambito + "_else");
        }

        else if (nodo instanceof ForNode f) {
            recorrer(f.init, ambito + "_for");
            recorrerLista(f.body, ambito + "_for");
        }

        else if (nodo instanceof SwitchNode s) {
            for (CaseNode c : s.cases) {
                recorrerLista(c.stmts, ambito + "_case");
            }
            recorrerLista(s.defaultStmts, ambito + "_default");
        }
    }
}