package com.example.graphwork;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Главный класс приложения для анализа графов.
 * Реализует графический интерфейс и управление логикой.
 */
public class HelloApplication extends Application {
    /**
     * Инициализация логгера
     */
    private static final Logger logger = LogManager.getLogger(HelloApplication.class);

    private TextField nodeInput;
    private TextField densityInput;
    private TextArea logArea;
    private Canvas canvas;
    private GraphModel currentGraph;

    public static void main(String[] args) {
        launch();
    }

    /**
     * Точка входа в JavaFX приложение.
     * Инициализирует сцену и компоненты интерфейса.
     *
     * @param stage основной контейнер окна (Stage).
     */
    @Override
    public void start(Stage stage) {
        logger.info("Запуск приложения...");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        HBox topPanel = new HBox(10);
        topPanel.setPadding(new Insets(0, 0, 10, 0));

        nodeInput = new TextField("5");
        nodeInput.setPrefWidth(80);
        densityInput = new TextField("30");
        densityInput.setPrefWidth(80);

        Button genBtn = new Button("Генерация");
        genBtn.setOnAction(e -> generateGraph());

        Button calcBtn = new Button("Расчет");
        calcBtn.setOnAction(e -> calculateLoops());

        topPanel.getChildren().addAll(new Label("Узлы:"), nodeInput,
                new Label("Плотность %:"), densityInput, genBtn, calcBtn);
        root.setTop(topPanel);

        Pane canvasPane = new Pane();
        canvas = new Canvas(800, 500);
        canvasPane.getChildren().add(canvas);
        canvasPane.setStyle("-fx-border-color: lightgray; -fx-background-color: #f4f4f4;");
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());
        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());
        root.setCenter(canvasPane);

        logArea = new TextArea();
        logArea.setPrefHeight(120);
        logArea.setEditable(false);
        root.setBottom(logArea);

        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Mason Graph Analyzer");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Обрабатывает нажатие кнопки генерации графа.
     * Считывает параметры и пересоздает модель графа.
     */
    private void generateGraph() {
        try {
            int nodes = Integer.parseInt(nodeInput.getText());
            int density = Integer.parseInt(densityInput.getText());

            if (nodes < 2 || nodes > 20) {
                logUi("Рекомендуется 2-20 узлов.");
                logger.warn("Пользователь выбрал нестандартное число узлов: " + nodes);
            }

            currentGraph = new GraphModel(nodes);
            currentGraph.generateRandomEdges(density);

            draw();
            logUi("Граф сгенерирован: " + nodes + " узлов, " + density + "%.");
            logger.info("Сгенерирован граф: nodes={}, density={}", nodes, density);
        } catch (NumberFormatException e) {
            logUi("Ошибка: Введите целые числа.");
            logger.error("Ошибка парсинга чисел", e);
        }
    }

    /**
     * Запускает расчет петель и замеряет время выполнения.
     */
    private void calculateLoops() {
        if (currentGraph == null) {
            logUi("Сначала создайте граф.");
            return;
        }

        logger.info("Начало расчета петель...");
        long start = System.nanoTime();
        List<List<Integer>> loops = currentGraph.findAllSimpleCycles();
        long end = System.nanoTime();

        double ms = (end - start) / 1_000_000.0;
        logUi("Найдено петель: " + loops.size() + ". Время: " + String.format("%.4f", ms) + " мс.");
        logger.info("Расчет завершен. Петель: {}, Время: {} мс", loops.size(), ms);

        loops.stream()
                .limit(5)
                .map(cycle -> cycle.stream()
                        .map(node -> node + 1)
                        .collect(Collectors.toList()))
                .forEach(cycle -> logUi("Петля: " + cycle));
        if (loops.size() > 5) {
            logUi("и ещё " + (loops.size() - 5) + " скрыто");
        }
    }

    /**
     * Выводит сообщение в текстовое поле интерфейса.
     * @param msg текст сообщения.
     */
    private void logUi(String msg) {
        logArea.appendText(msg + "\n");
    }

    /**
     * Отрисовывает текущий граф на Canvas.
     */
    private void draw() {
        if (currentGraph == null) return;

        double w = canvas.getWidth();
        double h = canvas.getHeight();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        int n = currentGraph.getNumNodes();
        double centerX = w / 2;
        double centerY = h / 2;
        double radius = Math.min(w, h) / 3;
        double nodeR = 18;

        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            x[i] = centerX + radius * Math.cos(angle);
            y[i] = centerY + radius * Math.sin(angle);
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (currentGraph.hasEdge(i, j)) {
                    drawArrow(gc, x[i], y[i], x[j], y[j], nodeR);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillOval(x[i] - nodeR, y[i] - nodeR, nodeR * 2, nodeR * 2);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x[i] - nodeR, y[i] - nodeR, nodeR * 2, nodeR * 2);
            gc.setFill(Color.WHITE);
            String label = String.valueOf(i + 1);
            double textOffset = label.length() > 1 ? 7 : 4;
            gc.fillText(label, x[i] - textOffset, y[i] + 4);
        }
    }

    /**
     * Рисует стрелку между двумя точками.
     * @param gc графический контекст.
     * @param x1 X начала.
     * @param y1 Y начала.
     * @param x2 X конца.
     * @param y2 Y конца.
     * @param r радиус узла (отступ).
     */
    private void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2, double r) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double startX = x1 + r * Math.cos(angle);
        double startY = y1 + r * Math.sin(angle);
        double endX = x2 - r * Math.cos(angle);
        double endY = y2 - r * Math.sin(angle);
        gc.strokeLine(startX, startY, endX, endY);
        double arrowSize = 10;
        gc.strokeLine(endX, endY, endX - arrowSize * Math.cos(angle - Math.PI/6), endY - arrowSize * Math.sin(angle - Math.PI/6));
        gc.strokeLine(endX, endY, endX - arrowSize * Math.cos(angle + Math.PI/6), endY - arrowSize * Math.sin(angle + Math.PI/6));
    }
}