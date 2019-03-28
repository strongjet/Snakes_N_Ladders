package Entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Game implements Serializable {

    private String board;
    private Map<Integer, Integer> snake = new HashMap<>();
    private Map<Integer, Integer> ladder = new HashMap<>();
    private int gridSize, rows, columns, die;

    public Game(String board, Map snake, Map ladder, int gridSize, int rows, int columns, int die) {

        this.board = board;
        this.snake = snake;
        this.ladder = ladder;
        this.gridSize = gridSize;
        this.rows = rows;
        this.columns = columns;
        this.die = die;

    }

    public String getBoard() {
        return this.board;
    }

    public Map getSnake() {
        return this.snake;
    }

    public Map getLadder() {
        return this.ladder;
    }

    public int getGridSize() {
        return this.gridSize;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public int getDie() {
        return this.die;
    }
}
