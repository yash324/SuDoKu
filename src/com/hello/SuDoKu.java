package com.hello;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SuDoKu {

    private int[][] solution;
    private Set<Integer> allValues;
    private Map<Position, Set<Integer>> possibleValues;
    private boolean changed = false;

    public SuDoKu(int[][] solution) {
        this.solution = solution;
        this.allValues = new HashSet<>();
        Integer[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Collections.addAll(allValues, values);
        setPossibleValues();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int testCases = Integer.parseInt(in.next());
        int[][] input = new int[9][9];
        while (testCases-- > 0) {
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    input[i][j] = Integer.parseInt(in.next());
                }
            SuDoKu solution = new SuDoKu(input);
            solution.print();
        }
        in.close();
    }

    public void calculateSolution() {
        do {
            changed = false;
            Set<Map.Entry<Position, Set<Integer>>> singlePossibleValueBoxes = possibleValues.entrySet().stream()
                    .filter(entry -> entry.getValue().size() == 1)
                    .collect(Collectors.toSet());
            if (singlePossibleValueBoxes.size() > 0) {
                changed = true;
                singlePossibleValueBoxes.forEach(entry -> {
                    int value = (int) entry.getValue().toArray()[0];
                    Position position = entry.getKey();
                    possibleValues.remove(position);
                    solution[position.getX()][position.getY()] = value;
                    addElementToGrid(position.getX(), position.getY(), value);
                });
            } else {
                checkRows();
                checkColumns();
                checkBoxes();
            }
        } while (changed);
    }

    private void checkRows() {
        IntStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8).forEach(row -> {
            Set<Map.Entry<Position, Set<Integer>>> boxesToSearch = possibleValues.entrySet().stream()
                    .filter(entry -> entry.getKey().getX() == row)
                    .collect(Collectors.toSet());
            checkForUniquePosition(boxesToSearch);
        });
    }

    private void checkColumns() {
        IntStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8).forEach(column -> {
            Set<Map.Entry<Position, Set<Integer>>> boxesToSearch = possibleValues.entrySet().stream()
                    .filter(entry -> entry.getKey().getY() == column)
                    .collect(Collectors.toSet());
            checkForUniquePosition(boxesToSearch);
        });
    }

    private void checkBoxes() {
        IntStream.of(0, 1, 2).forEach(row -> IntStream.of(0, 1, 2).forEach(column -> {
            Set<Map.Entry<Position, Set<Integer>>> boxesToSearch = possibleValues.entrySet().stream()
                    .filter(entry -> entry.getKey().getX() / 3 == row && entry.getKey().getY() / 3 == column)
                    .collect(Collectors.toSet());
            checkForUniquePosition(boxesToSearch);
        }));
    }

    private void checkForUniquePosition(Set<Map.Entry<Position, Set<Integer>>> boxesToSearch) {
        Set<Integer> missingValues = boxesToSearch.stream()
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        missingValues.forEach(missingValue -> {
            List<Map.Entry<Position, Set<Integer>>> boxesContainingValue = boxesToSearch.stream()
                    .filter(missingBox -> missingBox.getValue().contains(missingValue))
                    .collect(Collectors.toList());
            if (boxesContainingValue.size() == 1) {
                changed = true;
                Position position = boxesContainingValue.get(0).getKey();
                solution[position.getX()][position.getY()] = missingValue;
                possibleValues.remove(position);
                addElementToGrid(position.getX(), position.getY(), missingValue);

            }
        });
    }

    private void removePossibleValueForBox(int row, int column, int value) {
        possibleValues.entrySet().stream()
                .filter(entry -> entry.getKey().getX() / 3 == row / 3 && entry.getKey().getY() / 3 == column / 3)
                .forEach(entry -> entry.getValue().remove(value));
    }

    private void removePossibleValueForColumn(int column, int value) {
        possibleValues.entrySet().stream()
                .filter(entry -> entry.getKey().getY() == column)
                .forEach(entry -> entry.getValue().remove(value));
    }

    private void removePossibleValueForRow(Integer row, Integer value) {
        possibleValues.entrySet().stream()
                .filter(entry -> entry.getKey().getX() == row)
                .forEach(entry -> entry.getValue().remove(value));
    }

    private void addElementToGrid(int row, int column, int value) {
        removePossibleValueForRow(row, value);
        removePossibleValueForColumn(column, value);
        removePossibleValueForBox(row, column, value);
    }

    private void setPossibleValues() {
        possibleValues = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (solution[i][j] == 0) {
                    Position position = new Position(i, j);
                    Set<Integer> values = new HashSet<>(allValues);
                    possibleValues.put(position, values);
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (solution[i][j] != 0) {
                    addElementToGrid(i, j, solution[i][j]);
                }
            }
        }
    }

    private void print() {
        calculateSolution();
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                System.out.print(solution[i][j] + " ");
    }
}