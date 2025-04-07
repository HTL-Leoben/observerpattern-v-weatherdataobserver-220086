import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WeatherDataLogger implements WeatherDataObserver {

    final String FILENAME = "src/main/resources/textfiles/log.txt";
    BufferedWriter writer;
    FileWriter fileWriter;

    public WeatherDataLogger() {
        try {
            System.out.println("Initializing weather data logger");
            initializeComponents();

        } catch (IOException e) {
            System.out.println("Error initializing WeatherDataLogger");
            throw new RuntimeException(e);
        }
    }

    private void initializeComponents() throws IOException {
        fileWriter = new FileWriter(FILENAME);
        writer = new BufferedWriter(fileWriter);
    }

    @Override
    public void update(WeatherData weatherData) {
        try {
            writer.write(weatherData.toString());
            writer.newLine();
            writer.flush();
            System.out.println("Weather data written to " + FILENAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
