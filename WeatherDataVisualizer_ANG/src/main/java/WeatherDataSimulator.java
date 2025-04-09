import javafx.animation.AnimationTimer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class WeatherDataSimulator implements ObservableSimulator {

    // Löschen des Visualizers
//   private WeatherVisualizer visualizer;
    private Random random;
    private double lastTemperature;
    private int intervalMinutes;
    private LocalDateTime lastTimestamp;
    private Season currentSeason;
    private List<WeatherDataObserver> observers;

    // Weitere Überprüfung auf Non NUll -> throws?
    @Override
    public void addObserver(WeatherDataObserver observer) {
        Objects.requireNonNull(observer);
        observers.add(observer);

    }

    // Weitere Überprüfung auf Non NUll -> throws?
    @Override
    public void removeObserver(WeatherDataObserver observer) {
        Objects.requireNonNull(observer);
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(WeatherData weatherData) {
        for (WeatherDataObserver observer1 : observers) {
            observer1.update(weatherData);
        }
    }

    // Enum für Jahreszeiten bleibt unverändert
    public enum Season {
        SPRING(Month.MARCH, Month.MAY),
        SUMMER(Month.JUNE, Month.AUGUST),
        AUTUMN(Month.SEPTEMBER, Month.NOVEMBER),
        WINTER(Month.DECEMBER, Month.FEBRUARY);

        private final Month startMonth;
        private final Month endMonth;

        Season(Month startMonth, Month endMonth) {
            this.startMonth = startMonth;
            this.endMonth = endMonth;
        }

        public static Season getCurrentSeason(LocalDateTime dateTime) {
            Month month = dateTime.getMonth();
            for (Season season : values()) {
                if ((month.equals(season.startMonth) || month.compareTo(season.startMonth) > 0) &&
                        (month.equals(season.endMonth) || month.compareTo(season.endMonth) < 0)) {
                    return season;
                }
            }
            // Für Dezember (Winter über Jahreswechsel)
            return WINTER;
        }
    }

    // Added a new Linkedlist initialization to the Constructer to manage its Observers that got included with the ObservableSimulator Interface
    public WeatherDataSimulator(LocalDate startDate, int intervalMinutes) {
        this.random = new Random();
        this.intervalMinutes = intervalMinutes;
        this.lastTimestamp = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), 0, 0);
        this.currentSeason = Season.getCurrentSeason(lastTimestamp);
        this.lastTemperature = getInitialTemperatureForSeason(currentSeason);
        observers = new LinkedList<>();
        startWeatherDataSimulation();

    }

    // Self-explanatory will return a random Temp (Degrees in °C) depending on the season
    private double getInitialTemperatureForSeason(Season season) {
        switch (season) {
            case WINTER:
                // Temperaturbereich: -15 bis 15°C
                return random.nextDouble() * 30 - 15;
            case SPRING:
            case AUTUMN:
                // Temperaturbereich: 5 bis 25°C
                return random.nextDouble() * 20 + 5;
            case SUMMER:
                // Temperaturbereich: 15 bis 45°C
                return random.nextDouble() * 30 + 15;
            default:
                return 15.0;
        }
    }

    // Takes overall Conditions into consideration to calculateTemperatureChange as stated
    private double calculateTemperatureChange(WeatherCondition condition, LocalDateTime timestamp, Season season) {
        double baseChange = random.nextDouble() * 1.5 - 0.75;
        int hour = timestamp.getHour();

        // Temperaturänderung basierend auf Tageszeit
        if (hour >= 22 || hour < 6) {
            // Nachttemperaturen sinken
            baseChange -= (season == Season.SUMMER ? -1.5 : -0.5);
        } else if (hour >= 10 && hour < 16) {
            // Mittagshitze
            baseChange += 0.7;
        }

        // Saisonale Temperaturanpassungen und Grenzen
        switch (season) {
            case WINTER:
                // Begrenzen auf -15 bis 15°C
                if (lastTemperature > 15) baseChange -= 0.5;
                if (lastTemperature < -15) baseChange += 0.5;
                baseChange -= 0.3;
                break;
            case SUMMER:
                // Begrenzen auf 15 bis 45°C
                if (lastTemperature > 45) baseChange -= 1.5;
                if (lastTemperature < 15) baseChange += 1.5;
                baseChange += 0.3;
                break;
            case SPRING:
            case AUTUMN:
                // Begrenzen auf 5 bis 25°C
                if (lastTemperature > 25) baseChange -= 0.3;
                if (lastTemperature < 5) baseChange += 0.3;
                break;
        }

        // Spezielle Bedingungen für Temperaturänderung
        switch (condition) {
            case THUNDERSTORM:
                return baseChange - 4.5;
            case SNOW:
                return baseChange - 0.5;
            case SUNNY:
                return baseChange + 0.9;
            case CLOUDY:
                return baseChange - 1.3;
            case RAIN:
                return baseChange - 2.2;
            default:
                return baseChange;
        }
    }

    // Called in Constructor, implements an AnimationTimer and calls generateRealisticWeatherData in given Intervals
    // notifyObserver will be called and DataGenerated will be sent to its Observers
    private void startWeatherDataSimulation() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 2_000_000_000L) {
                    WeatherData currentWeather = generateRealisticWeatherData();
                    notifyObserver(currentWeather);
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    // Main Logic Method that returns a ready to use CurrentWeatherData Object
    private WeatherData generateRealisticWeatherData() {
        // Restliche Implementierung bleibt unverändert
        LocalDateTime newTimestamp = lastTimestamp.plusMinutes(intervalMinutes);
        Season newSeason = Season.getCurrentSeason(newTimestamp);

        WeatherCondition newCondition = generateWeatherCondition(newTimestamp, newSeason);

        double temperatureChange = calculateTemperatureChange(newCondition, newTimestamp, newSeason);
        double newTemperature = lastTemperature + temperatureChange;

        // Temperatur nach Saisonbereichen beschränken
        switch (newSeason) {
            case WINTER:
                newTemperature = Math.max(-15, Math.min(15, newTemperature));
                break;
            case SUMMER:
                newTemperature = Math.max(15, Math.min(45, newTemperature));
                break;
            case SPRING:
            case AUTUMN:
                newTemperature = Math.max(5, Math.min(25, newTemperature));
                break;
        }

        int rainProbability = calculateRainProbability(newCondition, newSeason, newTemperature);

        // Restliche Wetterdaten-Logik bleibt unverändert
        if (newCondition == WeatherCondition.SNOW && newTemperature > 2.0) {
            newCondition = WeatherCondition.CLOUDY;
        }

        if (newTemperature < 3.0) {
            newCondition = (random.nextDouble() < 0.5) ? WeatherCondition.SNOW : WeatherCondition.CLOUDY;
        }

        // Nutzung der Klasse CurrentWeatherData als Impl
        // Warum hier nicht mit Interface angesprochen?
        // WeatherData  - || -
        CurrentWeatherData weatherData = new CurrentWeatherData(
                newTemperature,
                rainProbability,
                newCondition,
                newTimestamp
        );
        lastTemperature = newTemperature;
        lastTimestamp = newTimestamp;
        currentSeason = newSeason;

        return weatherData;
    }

    // Restliche Methoden (generateWeatherCondition, calculateRainProbability) bleiben unverändert
    private WeatherCondition generateWeatherCondition(LocalDateTime timestamp, Season season) {
        int hour = timestamp.getHour();
        double conditionProbability = random.nextDouble();

        // Keine Sonne in der Nacht (22 Uhr bis 6 Uhr)
        if (hour >= 22 || hour < 6) {
            if (conditionProbability < 0.2) return WeatherCondition.THUNDERSTORM;
            if (conditionProbability < 0.5) return WeatherCondition.CLOUDY;
            return WeatherCondition.RAIN;
        }

        // Saisonale Wahrscheinlichkeiten
        switch (season) {
            case WINTER:
                if (conditionProbability < 0.3) return WeatherCondition.SNOW;
                if (conditionProbability < 0.6) return WeatherCondition.CLOUDY;
                return WeatherCondition.SUNNY;
            case SPRING:
                if (conditionProbability < 0.2) return WeatherCondition.RAIN;
                if (conditionProbability < 0.4) return WeatherCondition.CLOUDY;
                return WeatherCondition.SUNNY;
            case SUMMER:
                if (conditionProbability < 0.1) return WeatherCondition.THUNDERSTORM;
                if (conditionProbability < 0.3) return WeatherCondition.CLOUDY;
                return WeatherCondition.SUNNY;
            case AUTUMN:
                if (conditionProbability < 0.3) return WeatherCondition.RAIN;
                if (conditionProbability < 0.6) return WeatherCondition.CLOUDY;
                return WeatherCondition.SUNNY;
            default:
                return WeatherCondition.SUNNY;
        }
    }

    private int calculateRainProbability(WeatherCondition condition, Season season, double temperature) {
        // Keine Niederschläge unter 3 Grad Celsius
        if (temperature < 3.0) {
            // Bei kalten Temperaturen nur Schnee oder keine Niederschläge
            return condition == WeatherCondition.SNOW ? random.nextInt(41) + 30 : 0;
        }

        switch (season) {
            case WINTER:
                // Im Winter Schnee bei kalten Temperaturen
                if (condition == WeatherCondition.SNOW) return random.nextInt(41) + 30; // 30-70%
                return random.nextInt(21); // 0-20%
            case SPRING:
                // Frühling: höhere Regenwahrscheinlichkeit
                return random.nextInt(51) + 30; // 30-80%
            case SUMMER:
                // Sommer: unregelmäßige, aber intensive Niederschläge
                if (condition == WeatherCondition.THUNDERSTORM) return random.nextInt(51) + 50; // 50-100%
                return random.nextInt(31); // 0-30%
            case AUTUMN:
                // Herbst: häufige Niederschläge
                return random.nextInt(61) + 40; // 40-100%
            default:
                return random.nextInt(41);
        }
    }
}