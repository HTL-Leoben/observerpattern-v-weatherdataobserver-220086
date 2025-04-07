import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

public class WeatherVisualizationApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Visualizer erstellen
        WeatherVisualizer visualizer = new WeatherVisualizer();

        // Logger erstellen
        WeatherDataLogger logger = new WeatherDataLogger();

        // Simulator mit Visualizer und Logger verbinden
       WeatherDataSimulator simulator = new WeatherDataSimulator(LocalDate.of(2025, 6,1), 60);
       simulator.addObserver(visualizer);
       simulator.addObserver(logger);


        // Szene erstellen
        Scene scene = new Scene(visualizer.getRoot(), 800, 600);
        primaryStage.setTitle("Wetter-Visualisierung");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}