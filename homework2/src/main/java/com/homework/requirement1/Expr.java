package com.homework.requirement1;

public sealed interface Expr permits
        Expr.Constant, Expr.Negate, Expr.Exponent, Expr.Addition, Expr.Multiplication {

    double evaluate();

    record Constant(double value) implements Expr {
        @Override public double evaluate() {
            return value; }
        @Override public String toString() {
            return Double.toString(value); }
    }

    record Negate(Expr inner) implements Expr {
        @Override public double evaluate() { return -inner.evaluate(); }
        @Override public String toString() { return "-(" + inner + ")"; }
    }

    record Exponent(Expr base, double power) implements Expr {
        @Override public double evaluate() { return Math.pow(base.evaluate(), power); }
        @Override public String toString() { return "(" + base + ")^" + power; }
    }

    record Addition(Expr left, Expr right) implements Expr {
        @Override public double evaluate() { return left.evaluate() + right.evaluate(); }
        @Override public String toString() { return "(" + left + " + " + right + ")"; }
    }

    record Multiplication(Expr left, Expr right) implements Expr {
        @Override public double evaluate() { return left.evaluate() * right.evaluate(); }
        @Override public String toString() { return "(" + left + " * " + right + ")"; }
    }
}