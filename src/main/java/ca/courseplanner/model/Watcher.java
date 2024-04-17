package ca.courseplanner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model Class used to represent a watcher that monitors events related to
 * offering sections
 * Tracks when new offering sections of the department + course is made
 */

public class Watcher implements CourseWatcher {
    private long id;
    private static long nextId = 1;
    private Department department;
    private Course course;
    private List<String> events;

    public Watcher(Department department, Course course) {
        this.id = nextId++;
        this.department = department;
        this.course = course;
        this.events = new ArrayList<>();
        course.addWatcher(this);
    }

    @Override
    public void newEvent(String event) {
        events.add(event);
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }
    public void addEvent(String event){
        events.add(event);
    }



}
