package org.github.schmittjoaopedro.presentation;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class UseCase2 {

    public static void main(String[] args) {

        Model model = new Model();
        final int LOW = 0, MEDIUM = 1, HIGH = 2;
        final int SHORT_TERM = 0, MEDIUM_TERM = 1, LONG_TERM = 2;
        final int EQUITY_FUND = 0, INVESTMENT_FUND = 1, BANK_BOOK = 2;

        // Variables and domains
        IntVar WR = model.intVar("willingnessToRisk", new int[]{LOW, MEDIUM, HIGH});
        IntVar ID = model.intVar("investmentDuration", new int[]{SHORT_TERM, MEDIUM_TERM, LONG_TERM});
        IntVar RR = model.intVar("expectedReturnRate", new int[]{LOW, MEDIUM, HIGH});
        IntVar PN = model.intVar("productName", new int[]{EQUITY_FUND, INVESTMENT_FUND, BANK_BOOK});

        // Financial Service Constraints
        WR.eq(LOW).imp(PN.eq(BANK_BOOK)).post();
        WR.eq(MEDIUM).imp(PN.ne(EQUITY_FUND)).post();
        ID.eq(SHORT_TERM).imp(PN.eq(BANK_BOOK)).post();
        ID.eq(MEDIUM_TERM).imp(PN.ne(EQUITY_FUND)).post();
        RR.eq(HIGH).or(RR.eq(MEDIUM)).imp(PN.ne(BANK_BOOK)).post();
        WR.eq(LOW).and(RR.eq(HIGH)).not().post();
        ID.eq(SHORT_TERM).and(RR.eq(HIGH)).not().post();
        WR.eq(HIGH).and(RR.eq(LOW)).not().post();

        // User requirements
        WR.eq(MEDIUM).post();
        ID.eq(LONG_TERM).post();

        // Finds first feasible solution
        //model.getSolver().solve();
        //printVars(WR, ID, RR, PN);

        // Finds all feasible solutions
        //while (model.getSolver().solve()) printVars(WR, ID, RR, PN);
    }

    public static void printVars(IntVar WR, IntVar ID, IntVar RR, IntVar PN) {
        System.out.printf("%s, %s, %s, %s%n", WR, ID, RR, PN);
    }
}
