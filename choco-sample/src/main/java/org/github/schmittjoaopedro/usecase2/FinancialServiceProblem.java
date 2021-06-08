package org.github.schmittjoaopedro.usecase2;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.learn.XParameters;
import org.chocosolver.solver.search.loop.learn.LearnSignedClauses;
import org.chocosolver.solver.variables.IntVar;
import org.github.schmittjoaopedro.Utils;

public class FinancialServiceProblem {

    private static Model model;
    private static Utils utils;
    private static Solver solver;

    private static final int LOW = 0;
    private static final int MEDIUM = 1;
    private static final int HIGH = 2;

    private static final int SHORT_TERM = 0;
    private static final int MEDIUM_TERM = 1;
    private static final int LONG_TERM = 2;

    private static final int EQUITY_FUND = 0;
    private static final int INVESTMENT_FUND = 1;
    private static final int BANK_BOOK = 2;

    private static Model createModel() {
        // MODELLING
        model = new Model("Financial Service Problem");
        utils = new Utils(model);
        solver = model.getSolver();
        // Variables and domains
        IntVar willingnessToRisk = model.intVar("willingnessToRisk", new int[]{LOW, MEDIUM, HIGH});
        IntVar investmentDuration = model.intVar("investmentDuration", new int[]{SHORT_TERM, MEDIUM_TERM, LONG_TERM});
        IntVar expectedReturnRate = model.intVar("expectedReturnRate", new int[]{LOW, MEDIUM, HIGH});
        IntVar productName = model.intVar("productName", new int[]{EQUITY_FUND, INVESTMENT_FUND, BANK_BOOK});
        // Financial Service Constraints
        willingnessToRisk.eq(LOW).imp(productName.eq(BANK_BOOK)).post();
        willingnessToRisk.eq(MEDIUM).imp(productName.ne(EQUITY_FUND)).post();
        investmentDuration.eq(SHORT_TERM).imp(productName.eq(BANK_BOOK)).post();
        investmentDuration.eq(MEDIUM_TERM).imp(productName.ne(EQUITY_FUND)).post();
        expectedReturnRate.eq(HIGH).or(expectedReturnRate.eq(MEDIUM)).imp(productName.ne(BANK_BOOK)).post();
        willingnessToRisk.eq(LOW).and(expectedReturnRate.eq(HIGH)).not().post();
        investmentDuration.eq(SHORT_TERM).and(expectedReturnRate.eq(HIGH)).not().post();
        willingnessToRisk.eq(HIGH).and(expectedReturnRate.eq(LOW)).not().post();

        return model;
    }

    public static void main(String[] args) {

        // First solution
        System.out.println("\nFirst solution");
        model = createModel();
        solver.solve();
        printSolution();

        // All solutions
        System.out.println("\nAll solutions");
        model = createModel();
        while (solver.solve()) {
            printSolution();
        }

        // Find restricted solutions
        System.out.println("\nAll solutions given willingnessToRisk = LOW and expectedReturnRate = LOW");
        model = createModel();
        utils.getIntVar("willingnessToRisk").eq(LOW).post(); // New constraint
        utils.getIntVar("investmentDuration").eq(SHORT_TERM).post(); // New constraint
        while (solver.solve()) {
            printSolution();
        }

        // Explain contradiction
        System.out.println("\nExplain why [willingnessToRisk = LOW, expectedReturnRate = LOW, investmentDuration = SHORT_TERM] doesn't work");
        model = createModel();
        utils.getIntVar("willingnessToRisk").eq(LOW).post(); // New constraint
        utils.getIntVar("investmentDuration").eq(SHORT_TERM).post(); // New constraint
        utils.getIntVar("expectedReturnRate").eq(HIGH).post(); // New constraint
        try {
            solver.setLearningSignedClauses();
            XParameters.PROOF = true;
            solver.propagate();
        } catch (ContradictionException c) {
            ((LearnSignedClauses) solver.getLearner()).getExplanation().learnSignedClause(c);
        }

    }

    private static void printSolution() {
        System.out.print(utils.getEnumVarFormatted("willingnessToRisk", "LOW", "MEDIUM", "HIGH") + ", ");
        System.out.print(utils.getEnumVarFormatted("investmentDuration", "SHORT_TERM", "MEDIUM_TERM", "LONG_TERM") + ", ");
        System.out.print(utils.getEnumVarFormatted("expectedReturnRate", "LOW", "MEDIUM", "HIGH") + ", ");
        System.out.print(utils.getEnumVarFormatted("productName", "EQUITY_FUND", "INVESTMENT_FUND", "BANK_BOOK") + "\n");
    }
}
