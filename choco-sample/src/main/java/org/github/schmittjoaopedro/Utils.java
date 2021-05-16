package org.github.schmittjoaopedro;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

public class Utils {

    public static String getVarFormatted(Model model, String name, String... descriptions) {
        return getVarDesc(getVar(model, name), descriptions);
    }

    public static IntVar getVar(Model model, String name) {
        IntVar var = null;
        for (Variable v : model.getVars()) {
            if (name.equals(v.getName())) {
                var = (IntVar) v;
            }
        }
        return var;
    }

    public static String getVarDesc(IntVar var, String... descriptions) {
        String desc = var.getName() + "=";
        for (int i = 0; i < descriptions.length; i++) {
            if (i == var.getValue()) {
                desc += descriptions[i];
                break;
            }
        }
        return desc;
    }


}
