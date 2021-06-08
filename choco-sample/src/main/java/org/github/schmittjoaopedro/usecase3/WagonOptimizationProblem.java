package org.github.schmittjoaopedro.usecase3;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.github.schmittjoaopedro.Utils;

public class WagonOptimizationProblem {

    private static Model model;
    private static Utils utils;
    private static Solver solver;
    private static Solution solution;

    private static final int BLUE = 0;
    private static final int RED = 1;
    private static final int WHITE = 2;
    private static final int NO_COLOR = 3;

    private static final int STANDARD = 0;
    private static final int PREMIUM = 1;
    private static final int SPECIAL = 2;
    private static final int NO_TYPE = 3;

    private static int MAX_SEATS = 200;

    private static Model createModel() {
        // MODELLING
        model = new Model("Wagon Model");
        utils = new Utils(model);
        solver = model.getSolver();
        // Wagon
        RealVar length_mm = model.realVar("wagon.length_mm", 10000.0, 20000.0, 1e-6);
        IntVar nr_passengers = model.intVar("wagon.nr_passengers", 50, 200, true);
        IntVar nr_seats = model.intVar("wagon.nr_seats", 0, MAX_SEATS, true);
        IntVar nr_handrails = model.intVar("wagon.nr_handrails", 0, 1, true);
        IntVar standing_room = model.intVar("wagon.standing_room", 0, 200, true);
        RealVar used_space = model.realVar("wagon.used_space", 0.0, Double.MAX_VALUE, 1e-6);
        // Handrail
        IntVar handrail_type = model.intVar("handrail.type", new int[]{STANDARD, PREMIUM, SPECIAL, NO_TYPE});
        // Seats
        IntVar[] seat_color = model.intVarArray("seat.color", MAX_SEATS, new int[]{BLUE, RED, WHITE, NO_COLOR});
        IntVar[] seat_type = model.intVarArray("seat.type", MAX_SEATS, new int[]{STANDARD, PREMIUM, SPECIAL, NO_TYPE});

        // Constrain numbers
        // nr_seats + standing_room = nr_passengers
        nr_seats.add(standing_room).eq(nr_passengers).post();
        // nr_seats + standing_room / 3 <= length_mm * 4 / 1000
        utils.castReal(nr_seats).add(utils.castReal(standing_room).div(3.0)).le(length_mm.mul(4.0).div(1000.0)).equation().post();

        // Mandatory handrail for standing room with proper type
        // standing_room > 0 -> nr_handrails = 1
        standing_room.gt(0).imp(nr_handrails.eq(1)).post();
        // handrail_type != special
        handrail_type.ne(SPECIAL).post();
        // nr_handrails = 0 <-> handrail_type = noType
        nr_handrails.eq(0).iff(handrail_type.eq(NO_TYPE)).post();
        // nr_handrails > 0 -> forall (i in 1..nr_seats where seat_type[i] != SPECIAL) (handrail_type = seat.type[i])
        for (int i = 0; i < MAX_SEATS; i++) {
            nr_handrails.gt(0).and(nr_seats.gt(i)).and(seat_type[i].ne(SPECIAL)).imp(handrail_type.eq(seat_type[i])).post();
        }

        // Same color and type for all seats but special
        // forall (i in nr_seats+1..max_seats) (seat_color[i] = noColor)
        // forall (i in nr_seats+1..max_seats) (seat_type[i] = noType)
        for (int i = 0; i < MAX_SEATS; i++) {
            nr_seats.le(i).imp(seat_color[i].eq(NO_COLOR)).post();
            nr_seats.le(i).imp(seat_type[i].eq(NO_TYPE)).post();
        }
        // forall (i,j in 1..nr_seats where i<j) (seat_type[i] != special /\ seat_type[j] != special -> seat_type[i] = seat_type[j])
        // forall (i,j in 1..nr_seats where i<j) (seat_type[i] != special /\ seat_type[j] != special -> seat_color[i] = seat_color[j])
        for (int j = 0; j < MAX_SEATS; j++) {
            for (int i = 0; i < j; i++) {
                nr_seats.gt(j).and(seat_type[i].ne(SPECIAL)).and(seat_type[j].ne(SPECIAL)).imp(seat_type[i].eq(seat_type[j])).post();
                nr_seats.gt(j).and(seat_type[i].ne(SPECIAL)).and(seat_type[j].ne(SPECIAL)).imp(seat_color[i].eq(seat_color[j])).post();
            }
        }
        // forall (i in 1..nr_seats) (seat_type[i] = special -> seat_color = red)
        for (int i = 0; i < MAX_SEATS; i++) {
            nr_seats.gt(i).and(seat_type[i].eq(SPECIAL)).imp(seat_color[i].eq(RED)).post();
        }

        // Use full length for passengers (avoid dead space)
        used_space.eq(utils.castReal(nr_passengers).div(length_mm)).equation().post();
        model.setObjective(true, used_space);

        return model;
    }

    public static void main(String[] args) throws ContradictionException {

        // Optimize solution (everyone standing)
        model = createModel();
        solution = new Solution(model);
        utils.getIntVar("wagon.nr_passengers").eq(160).post(); // User constraint
        // Apply constraints
        System.out.println("------------- Options -------------");
        solver.propagate();
        printSolution();
        // Optimize solution
        System.out.println("------------- Solution -------------");
        while (solver.solve()) {
            System.out.println("Maximizing " + utils.getRealVarFormatted("wagon.used_space"));
            solution.record();
        }
        solution.restore();
        printSolution();
        System.out.println("------------------------------------");

        // Optimize solution (with seats)
        model = createModel();
        solution = new Solution(model);
        utils.getIntVar("wagon.nr_passengers").eq(160).post(); // User constraint
        utils.getIntVar("wagon.nr_seats").eq(30).post(); // User constraint
        // Apply constraints
        System.out.println("------------- Options -------------");
        solver.propagate();
        printSolution();
        // Optimize solution
        System.out.println("------------- Solution -------------");
        while (solver.solve()) {
            System.out.println("Maximizing " + utils.getRealVarFormatted("wagon.used_space"));
            solution.record();
        }
        solution.restore();
        printSolution();
        System.out.println("------------------------------------");

        // Best solution from all
        model = createModel();
        solution = new Solution(model);
        System.out.println("------------- Solution -------------");
        while (solver.solve()) {
            System.out.println("Maximizing " + utils.getRealVarFormatted("wagon.used_space"));
            solution.record();
        }
        solution.restore();
        printSolution();
        System.out.println("------------------------------------");

    }

    private static void printSolution() {
        System.out.println(utils.getRealVarFormatted("wagon.length_mm"));
        System.out.println(utils.getRealVarFormatted("wagon.used_space"));
        System.out.println(utils.getIntVarFormatted("wagon.nr_passengers"));
        System.out.println(utils.getIntVarFormatted("wagon.nr_seats"));
        System.out.println(utils.getIntVarFormatted("wagon.nr_handrails"));
        System.out.println(utils.getIntVarFormatted("wagon.standing_room"));
        System.out.println(utils.getEnumVarFormatted("handrail.type", "STANDARD", "PREMIUM", "SPECIAL", "NO_TYPE"));
        String prev = "";
        for (int i = 0; i < MAX_SEATS; i++) {
            String seat = "type = " + utils.getVarDesc("seat.type[" + i + "]", "STANDARD", "PREMIUM", "SPECIAL", "NO_TYPE") + ", " +
                    "color = " + utils.getVarDesc("seat.color[" + i + "]", "BLUE", "RED", "WHITE", "NO_COLOR");
            if (!prev.equals(seat)) {
                System.out.println("seat[" + i + "] " + seat);
                prev = seat;
            }
        }
    }
}
