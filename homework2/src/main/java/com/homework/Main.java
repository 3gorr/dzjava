package com.homework;

import com.homework.requirement1.Expr;
import com.homework.requirement2.IntDynamicArray;
import com.homework.requirement3.*;
import com.homework.requirement4.*;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        System.out.println("REQUIREMENT 1");
        testRequirement1();

        System.out.println("\nREQUIREMENT 2");
        testRequirement2();

        System.out.println("\nREQUIREMENT 3");
        testRequirement3();

        System.out.println("\nREQUIREMENT 4");
        testRequirement4();
    }

    private static void testRequirement1() {
        var two = new Expr.Constant(2);
        var four = new Expr.Constant(4);
        var negOne = new Expr.Negate(new Expr.Constant(1));
        var sumTwoFour = new Expr.Addition(two, four);
        var mult = new Expr.Multiplication(sumTwoFour, negOne);
        var exp = new Expr.Exponent(mult, 2);
        var res = new Expr.Addition(exp, new Expr.Constant(1));

        System.out.println(res + " = " + res.evaluate());

        Expr e1 = new Expr.Addition(new Expr.Constant(1.5), new Expr.Constant(2.5)); // 4.0
        Expr e2 = new Expr.Multiplication(new Expr.Negate(new Expr.Constant(3)), new Expr.Constant(10)); // -30
        Expr e3 = new Expr.Exponent(new Expr.Constant(9), 0.5); // 3

        System.out.println(e1 + " = " + e1.evaluate());
        System.out.println(e2 + " = " + e2.evaluate());
        System.out.println(e3 + " = " + e3.evaluate());
    }

    private static void testRequirement2() {
        var arr = new IntDynamicArray(2);
        System.out.println("Initially: " + arr + ", size=" + arr.size() + ", empty=" + arr.isEmpty());
        arr.add(10);
        arr.add(20);
        arr.add(30);
        arr.add(40);
        System.out.println("After add 10,20,30,40: " + arr + ", size=" + arr.size());

        System.out.println("get(2) = " + arr.get(2));
        int prev = arr.set(2, 300);
        System.out.println("set(2,300) prev=" + prev + " => " + arr);

        arr.add(0, 111);
        arr.add(arr.size(), 999);
        arr.add(3, 222);
        System.out.println("After add(index): " + arr);

        arr.add(222);
        arr.add(222);
        System.out.println("After adding duplicates 222: " + arr);
        System.out.println("contains(300)=" + arr.contains(300));
        System.out.println("indexOf(222)=" + arr.indexOf(222));
        System.out.println("lastIndexOf(222)=" + arr.lastIndexOf(222));
        System.out.println("indexOf(777)=" + arr.indexOf(777));

        int removed0 = arr.remove(0);
        int removedMid = arr.remove(2);
        int removedLast = arr.remove(arr.size() - 1);
        System.out.println("Removed: first=" + removed0 + ", mid=" + removedMid + ", last=" + removedLast);
        System.out.println("After remove: " + arr);

        var other = new IntDynamicArray();
        other.add(20);
        other.add(300);
        other.add(555);

        System.out.println("Other: " + other);
        System.out.println("arr.containsAll(other) = " + arr.containsAll(other));

        boolean changed = arr.addAll(other);
        System.out.println("arr.addAll(other) changed=" + changed + " => " + arr);

        var insert = new IntDynamicArray();
        insert.add(-1);
        insert.add(-2);
        insert.add(-3);

        arr.addAll(2, insert);
        System.out.println("arr.addAll(2, [-1,-2,-3]) => " + arr);

        var toRemove = new IntDynamicArray();
        toRemove.add(20);
        toRemove.add(-2);
        toRemove.add(999999);

        boolean removedAny = arr.removeAll(toRemove);
        System.out.println("arr.removeAll([20,-2,999999]) changed=" + removedAny + " => " + arr);

        var toKeep = new IntDynamicArray();
        toKeep.add(-1);
        toKeep.add(300);
        toKeep.add(555);

        boolean retainedAny = arr.retainAll(toKeep);
        System.out.println("arr.retainAll([-1,300,555]) changed=" + retainedAny + " => " + arr);

        arr.add(1000);
        arr.add(-999);
        System.out.println("Before sort: " + arr);
        arr.sort();
        System.out.println("After sort:  " + arr);

        arr.clear();
        System.out.println("After clear: " + arr + ", size=" + arr.size() + ", empty=" + arr.isEmpty());

        try {
            arr.get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("OK: get(0) on empty threw: " + e.getMessage());
        }

        try {
            arr.add(5, 123);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("OK: add(5,123) on empty threw: " + e.getMessage());
        }
    }

    private static void testRequirement3() {
        var stableManager = new ConnectionManager() {
            @Override
            public Connection getConnection() {
                return new StableConnection();
            }
        };

        var exec1 = new PopularCommandExecutor(stableManager, 3);
        exec1.updatePackages();

        var alwaysFaulty = new FaultyConnectionManager(new Random(42), 1.0);
        var exec2 = new PopularCommandExecutor(alwaysFaulty, 3);

        try {
            exec2.updatePackages();
            System.out.println("ERROR: expected failure, but command succeeded (unexpected)");
        } catch (ConnectionException e) {
            System.out.println("OK: failed as expected: " + e.getMessage());
            System.out.println("Cause preserved: " + (e.getCause() != null));
        }

        var defaultMgr = new DefaultConnectionManager(new Random(123), 0.7, 0.5);
        var exec3 = new PopularCommandExecutor(defaultMgr, 5);

        try {
            exec3.updatePackages();
            System.out.println("OK: succeeded within retries");
        } catch (ConnectionException e) {
            System.out.println("Could not succeed within retries: " + e.getMessage());
        }
    }

    private static void testRequirement4() {
        testStrategy("DoublingStrategy", new DoublingStrategy());
        testStrategy("FixedIncrementStrategy(+3)", new FixedIncrementStrategy(3));
        testStrategy("GoldenRatioStrategy", new GoldenRatioStrategy());
    }

    private static void testStrategy(String name, CapacityStrategy strategy) {
        var arr = new IntDynamicArray(1, strategy);
        for (int i = 1; i <= 12; i++) {
            arr.add(i);
        }
        System.out.println(name + " => " + arr + " (size=" + arr.size() + ")");
    }
}