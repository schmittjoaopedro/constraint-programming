package org.github.schmittjoaopedro;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.Variable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utils {

    private Model model;

    private Map<String, Variable> variableMap = new HashMap<>();

    public Utils(Model model) {
        this.model = model;
    }

    public String getEnumVarFormatted(String name, String... descriptions) {
        return name + " = " + getVarDesc(getIntVar(name), descriptions);
    }

    public String getIntVarFormatted(String name) {
        IntVar var = getIntVar(name);
        if (var.isInstantiated()) {
            return name + " = " + var.getUB();
        } else {
            return name + " = [" + var.getLB() + ", " + var.getUB() + "]";
        }
    }

    public String getRealVarFormatted(String name) {
        RealVar var = getRealVar(name);
        if (var.isInstantiated()) {
            return name + " = " + String.format("%.6f", var.getUB());
        } else {
            return name + " = [" + String.format("%.6f", var.getLB()) + ", " + String.format("%.6f", var.getUB()) + "]";
        }
    }

    public IntVar getIntVar(String name) {
        if (variableMap.isEmpty()) {
            for (Variable var : model.getVars()) {
                variableMap.put(var.getName(), var);
            }
        }
        IntVar var = null;
        for (Variable v : model.getVars()) {
            if (name.equals(v.getName())) {
                var = (IntVar) v;
                break;
            }
        }
        return var;
    }

    public RealVar getRealVar(String name) {
        if (variableMap.isEmpty()) {
            for (Variable var : model.getVars()) {
                variableMap.put(var.getName(), var);
            }
        }
        RealVar var = null;
        for (Variable v : model.getVars()) {
            if (name.equals(v.getName())) {
                var = (RealVar) v;
                break;
            }
        }
        return var;
    }

    public RealVar castReal(IntVar intVar) {
        RealVar realVar = intVar.getModel().realVar(intVar.getLB(), intVar.getUB(), 1e-6);
        intVar.getModel().eq(realVar, intVar).post();
        return realVar;
    }

    public String getVarDesc(String name, String... descriptions) {
        return getVarDesc(getIntVar(name), descriptions);
    }

    public String getVarDesc(IntVar var, String... descriptions) {
        String desc = "";
        if (var.isInstantiated()) {
            desc = descriptions[var.getValue()];
        } else {
            Iterator<Integer> iter = var.iterator();
            desc += "[";
            while (iter.hasNext()) {
                desc += descriptions[iter.next()];
                if (iter.hasNext()) {
                    desc += ", ";
                }
            }
            desc += "]";
        }
        return desc;
    }


}
