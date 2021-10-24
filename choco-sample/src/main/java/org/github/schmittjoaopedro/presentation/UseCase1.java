package org.github.schmittjoaopedro.presentation;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class UseCase1 {

    public static void main(String[] args) {

        final int RED = 0, GREEN = 1, BLUE = 2;
        Model model = new Model();

        IntVar WA  = model.intVar("WA",  new int[]{RED, GREEN, BLUE});
        IntVar NT  = model.intVar("NT",  new int[]{RED, GREEN, BLUE});
        IntVar SA  = model.intVar("SA",  new int[]{RED, GREEN, BLUE});
        IntVar Q   = model.intVar("Q",   new int[]{RED, GREEN, BLUE});
        IntVar NSW = model.intVar("NSW", new int[]{RED, GREEN, BLUE});
        IntVar V   = model.intVar("V",   new int[]{RED, GREEN, BLUE});
        IntVar T   = model.intVar("T",   new int[]{RED, GREEN, BLUE});

        WA.ne(NT).post();
        WA.ne(SA).post();
        NT.ne(Q).post();
        NT.ne(SA).post();
        SA.ne(Q).post();
        SA.ne(NSW).post();
        SA.ne(V).post();
        Q.ne(NSW).post();
        NSW.ne(V).post();

        // Find first feasible solution
        //model.getSolver().solve();
        //printVars(WA, NT, SA, Q, NSW, V, T);

        // Find all feasible solutions
        //while (model.getSolver().solve()) printVars(WA, NT, SA, Q, NSW, V, T);

        // Find all solutions restricted by WA = BLUE
        WA.eq(BLUE).post();
        while (model.getSolver().solve()) printVars(WA, NT, SA, Q, NSW, V, T);

    }

    public static void printVars(IntVar WA, IntVar NT, IntVar SA, IntVar Q, IntVar NSW, IntVar V, IntVar T) {
        System.out.printf("%s, %s, %s, %s, %s, %s, %s%n", WA, NT, SA, Q, NSW, V, T);
    }

}
