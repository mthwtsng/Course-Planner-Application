package ca.courseplanner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for representing Courses
 * Contains 1-to-many courseOfferings
 */

public class Course implements Observe {
    private static int nextId = 1;
    private String subject;
    private String catalogNumber;
    private final List<CourseOffering> courseOfferings = new ArrayList<>();
    private final List<Watcher> watchers = new ArrayList<>();
    private int courseId;

    public Course(String subject, String catalogNumber) {
        this.subject = subject;
        this.catalogNumber = catalogNumber;
        this.courseId = nextId++;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public void addCourseOffering(CourseOffering courseOffering) {
        courseOfferings.add(courseOffering);
    }

    public List<CourseOffering> getAllCourseOfferings() {
        return courseOfferings;
    }
    public int getCourseId() {
        return courseId;
    }

    public CourseOffering getSameCourseOffering(CourseOffering courseOffering){
        for(CourseOffering offering : courseOfferings){
            if(courseOffering.getSemesterCode() == offering.getSemesterCode()
            && courseOffering.getLocation().equals(offering.getLocation())
            && courseOffering.getInstructors().equals(offering.getInstructors())){
                return offering;
            }
        }
        return null;
    }

    public CourseOffering getCourseOfferingById(int offeringId) {
        for (CourseOffering offering : courseOfferings) {
            if (offering.getCourseOfferingId() == offeringId) {
                return offering;
            }
        }
        return null;
    }

    public List<Watcher> getAllWatchers(){
        return watchers;
    }

    @Override
    public void addWatcher(Watcher watcher) {
        watchers.add(watcher);
    }

    @Override
    public void removeWatcher(Watcher watcher) {
        watchers.remove(watcher);
    }

    @Override
    public void notifyWatchers(String event) {
        for (Watcher watcher : watchers) {
            watcher.newEvent(event);
        }
    }

}




