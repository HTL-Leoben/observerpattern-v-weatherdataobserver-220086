public interface Observable {
    void addObserver(WeatherDataObserver observer);
    void removeObserver(WeatherDataObserver observer);
    void notifyObserver(WeatherData weatherData);

}
