package com.example.graphwork;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Модель данных графа.
 * Отвечает за хранение структуры данных (матрица смежности)
 * и выполнение алгоритмов генерации и поиска циклов.
 */
public class GraphModel {

    /** Количество узлов в графе. */
    private final int numNodes;

    /** Матрица смежности: matrix[i][j] == true, если есть путь i -> j. */
    private final boolean[][] matrix;

    /**
     * Конструктор модели графа.
     *
     * @param numNodes количество вершин (узлов).
     */
    public GraphModel(int numNodes) {
        this.numNodes = numNodes;
        this.matrix = new boolean[numNodes][numNodes];
    }

    /**
     * Генерирует случайные связи между узлами.
     * @param density вероятность создания связи в процентах (0-100).
     */
    public void generateRandomEdges(int density) {
        Random rand = new Random();
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (rand.nextInt(100) < density) {
                    matrix[i][j] = true;
                }
            }
        }
    }

    /**
     * Проверяет наличие ребра между узлами.
     * @param i индекс начального узла.
     * @param j индекс конечного узла.
     * @return true, если ребро существует.
     */
    public boolean hasEdge(int i, int j) {
        return matrix[i][j];
    }

    /**
     * Возвращает общее количество узлов графа.
     * @return целое число узлов.
     */
    public int getNumNodes() {
        return numNodes;
    }

    /**
     * Находит все простые (элементарные) замкнутые циклы в графе.
     * @return список списков целых чисел, представляющих пути циклов.
     */
    public List<List<Integer>> findAllSimpleCycles() {
        List<List<Integer>> cycles = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            dfs(i, i, new boolean[numNodes], new ArrayList<>(List.of(i)), cycles);
        }

        return cycles.stream()
                .filter(this::isCanonical)
                .sorted(Comparator.comparingInt(List::size))
                .collect(Collectors.toList());
    }

    /**
     * Рекурсивный метод поиска в глубину (DFS).
     */
    private void dfs(int start, int curr, boolean[] visited, List<Integer> path, List<List<Integer>> res) {
        visited[curr] = true;
        for (int next = 0; next < numNodes; next++) {
            if (matrix[curr][next]) {
                if (next == start) {
                    List<Integer> cycle = new ArrayList<>(path);
                    cycle.add(start);
                    res.add(cycle);
                } else if (!visited[next] && next > start) {
                    path.add(next);
                    dfs(start, next, visited, path, res);
                    path.remove(path.size() - 1);
                }
            }
        }
        visited[curr] = false;
    }

    /**
     * Проверяет, является ли найденный цикл каноническим.
     */
    private boolean isCanonical(List<Integer> cycle) {
        int start = cycle.get(0);
        return cycle.stream().noneMatch(n -> n < start);
    }
}