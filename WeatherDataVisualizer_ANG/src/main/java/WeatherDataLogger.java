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
        }
    }

    private void initializeComponents() throws IOException {
        fileWriter = new FileWriter(FILENAME);
        writer = new BufferedWriter(fileWriter);
    }

    @Override
    public void update(WeatherData weatherData) {
        try {
            // write new Information into the Log.txt
            writer.write(weatherData.toString());

            // goes into the next line
            writer.newLine();

            // flush stream
            writer.flush();

            // Message for successful Data Entry
            System.out.println("Weather data written to " + FILENAME);
        } catch (IOException e) {
            // IOException got catched and will be reported to console
            System.out.println("Error writing weather data to file" + e.getMessage() + e.getCause());
        }
    }
}
