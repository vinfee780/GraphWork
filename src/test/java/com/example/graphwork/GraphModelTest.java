package com.example.graphwork;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GraphModelTest {

    @Test
    void testEmptyGraphHasNoCycles() {
        GraphModel graph = new GraphModel(5);

        graph.generateRandomEdges(0);

        List<List<Integer>> cycles = graph.findAllSimpleCycles();

        assertNotNull(cycles);
        assertTrue(cycles.isEmpty());
    }

    @Test
    void testFullGraphHasCycles() {
        GraphModel graph = new GraphModel(3);

        graph.generateRandomEdges(100);

        List<List<Integer>> cycles = graph.findAllSimpleCycles();

        assertNotNull(cycles);
        assertFalse(cycles.isEmpty());
    }

    @Test
    void testNodeCountInitialization() {
        int expectedNodes = 10;
        GraphModel graph = new GraphModel(expectedNodes);

        assertEquals(expectedNodes, graph.getNumNodes());
    }

    @Test
    void testZeroDensityHasNoEdges() {
        int n = 10;
        GraphModel graph = new GraphModel(n);
        graph.generateRandomEdges(0);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                assertFalse(graph.hasEdge(i, j),
                        "При плотности 0 связей быть не должно");
            }
        }
    }

    @Test
    void testNegativeNodesThrowsException() {
        assertThrows(NegativeArraySizeException.class, () -> {
            new GraphModel(-5);
        }, "Создание графа с отрицательным числом узлов должно вызывать ошибку");
    }
}