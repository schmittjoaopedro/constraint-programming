package org.github.schmittjoaopedro.usecase1;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.learn.XParameters;
import org.chocosolver.solver.search.loop.learn.LearnSignedClauses;
import org.chocosolver.solver.variables.IntVar;
import org.github.schmittjoaopedro.Utils;

public class MapColoringProblem {

    private static Model model;
    private static Utils utils;
    private static Solver solver;

    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;

    private static Model createModel() {
        // MODELING
        model = new Model("Map coloring");
        utils = new Utils(model);
        solver = model.getSolver();
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
        model = createModel();
        solver.solve();
        printSolution();

        // Find all solutions
        System.out.println("\nAll solutions");
        model = createModel();
        while (solver.solve()) {
            printSolution();
        }

        // Find restricted solutions
        System.out.println("\nAll solutions given WA = GREEN");
        model = createModel();
        utils.getIntVar("WA").eq(GREEN).post(); // New constraint
        while (solver.solve()) {
            printSolution();
        }

        // Explain contradiction
        System.out.println("\nExplain why [WA = GREEN, NT = GREEN] doesn't work");
        model = createModel();
        utils.getIntVar("WA").eq(GREEN).post(); // New constraint
        utils.getIntVar("NT").eq(GREEN).post(); // New constraint
        try {
            XParameters.PROOF = true;
            solver.setLearningSignedClauses();
            solver.propagate();
        } catch (ContradictionException c) {
            ((LearnSignedClauses) solver.getLearner()).getExplanation().learnSignedClause(c);
        }
    }

    private static void printSolution() {
        System.out.print(utils.getEnumVarFormatted("WA", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(utils.getEnumVarFormatted("NT", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(utils.getEnumVarFormatted("SA", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(utils.getEnumVarFormatted("Q", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(utils.getEnumVarFormatted("NSW", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(utils.getEnumVarFormatted("V", "RED", "GREEN", "BLUE") + ", ");
        System.out.print(utils.getEnumVarFormatted("T", "RED", "GREEN", "BLUE") + "\n");
    }
}
