package ca.courseplanner.model;

public interface Observe {
    void addWatcher(Watcher observer);
    void removeWatcher(Watcher observer);
    void notifyWatchers(String event);
}
