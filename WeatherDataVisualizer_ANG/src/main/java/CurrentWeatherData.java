import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CurrentWeatherData implements WeatherData {
    private double temperature;
    private int rainProbability;
    private WeatherCondition currentCondition;
    private LocalDateTime timestamp;

    public CurrentWeatherData(double temperature, int rainProbability, WeatherCondition currentCondition, LocalDateTime timestamp) {
        this.temperature = temperature;
        this.rainProbability = rainProbability;
        this.currentCondition = currentCondition;
        this.timestamp = timestamp;

    }

    // Überlegung Builder/Factory Pattern
    // Konstruktor ohne time
    public CurrentWeatherData(double temperature, int rainProbability, WeatherCondition currentCondition) {
        this.temperature = temperature;
        this.rainProbability = rainProbability;
        this.currentCondition = currentCondition;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public int getRainProbability() {
        return rainProbability;
    }

    @Override
    public WeatherCondition getCurrentCondition() {
        return currentCondition;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Equals und HashCode Methoden für korrekte Objektvergleiche
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentWeatherData that = (CurrentWeatherData) o;
        return Double.compare(that.temperature, temperature) == 0 &&
                rainProbability == that.rainProbability &&
                currentCondition == that.currentCondition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, rainProbability, currentCondition);
    }

    // toString Methode für einfache Ausgabe
    @Override
    public String toString() {
        return String.format("Wetterdaten [Temperatur: %.1f°C, Regenwahrscheinlichkeit: %d%%, Bedingung: %s, Zeitpunkt: %s]",
                temperature, rainProbability, currentCondition, timestamp);
    }

}
