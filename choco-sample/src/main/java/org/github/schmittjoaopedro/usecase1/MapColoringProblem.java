package org.github.schmittjoaopedro.usecase1;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.learn.XParameters;
import org.chocosolver.solver.search.loop.learn.LearnSignedClauses;
import org.chocosolver.solver.variables.IntVar;
import org.github.schmittjoaopedro.Utils;

import java.util.Arrays;

public class MapColoringProblem {

    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;

    private static Model createModel() {
        // MODELING
        Model model = new Model("Map coloring");
        // Variables and domain
        IntVar WA = model.intVar("WA", new int[]{RED, GREEN, BLUE});
        IntVar NT = model.intVar("NT", new int[]{RED, GREEN, BLUE});
        IntVar SA = model.intVar("SA", new int[]{RED, GREEN, BLUE});
        IntVar Q = model.intVar("Q", new int[]{RED, GREEN, BLUE});
        IntVar NSW = model.intVar("NSW", new int[]{RED, GREEN, BLUE});
        IntVar V = model.intVar("V", new int[]{RED, GREEN, BLUE});
        IntVar T = model.intVar("T", new int[]{RED, GREEN, BLUE});
        // Constraints
        WA.ne(NT).post();
        WA.ne(SA).post();
        NT.ne(Q).post();
        NT.ne(SA).post();
        SA.ne(Q).post();
        SA.ne(NSW).post();
        SA.ne(V).post();
        Q.ne(NSW).post();
        NSW.ne(V).post();

        return model;
    }

    public static void main(String[] args) {

        // Find first solution
        System.out.println("\nFirst solution");
        Model model = createModel();
        model.getSolver().solve();
        printSolution(model);

        // Find all solutions
        System.out.println("\nAll solutions");
        model = createModel();
        while (model.getSolver().solve()) {
            printSolution(model);
        }

        // Find restricted solutions
        System.out.println("\nAll solutions given WA = GREEN");
        model = createModel();
        Utils.getVar(model, "WA").eq(GREEN).post(); // New constraint
        while (model.getSolver().solve()) {
            printSolution(model);
        }

        // Explain contradiction
        System.out.println("\nExplain why [WA = GREEN, NT = GREEN] doesn't work");
        model = createModel();
        Utils.getVar(model, "WA").eq(GREEN).post(); // New constraint
        Utils.getVar(model, "NT").eq(GREEN).post(); // New constraint
        try {
            XParameters.PROOF = true;
            model.getSolver().setLearningSignedClauses();
            model.getSolver().propagate();
        } catch (ContradictionException c) {
            ((LearnSignedClauses) model.getSolver().getLearner()).getExplanation().learnSignedClause(c);
        }
    }

    private static void printSolution(Model model) {
        System.out.print(Utils.getVarFormatted(model, "WA", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(Utils.getVarFormatted(model, "NT", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(Utils.getVarFormatted(model, "SA", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(Utils.getVarFormatted(model, "Q", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(Utils.getVarFormatted(model, "NSW", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(Utils.getVarFormatted(model, "V", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(Utils.getVarFormatted(model, "T", "RED", "GREEN", "BLUE") + "\n");
    }
}
