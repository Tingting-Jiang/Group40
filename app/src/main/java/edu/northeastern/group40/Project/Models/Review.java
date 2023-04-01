package edu.northeastern.group40.Project.Models;

// enum class for vehicle review

public enum Review {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

    private int value;

    Review(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}