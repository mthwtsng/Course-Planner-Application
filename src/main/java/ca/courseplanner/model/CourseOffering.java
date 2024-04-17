package ca.courseplanner.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Model class representing a course offering in a department
 *  Contains 1-to-many Offering Sections
 */

public class CourseOffering {
    private static int nextId = 1;
    private Semester semester;
    private long semesterCode;
    private String location;
    private String instructors;
    private List<OfferingSection> offeringSections;
    private int courseOfferingId;
    private List<Watcher> watchers;

    public CourseOffering(long semesterCode, String location, String instructors) {
        this.semester = new Semester(semesterCode);
        this.semesterCode = semesterCode;
        this.location = location;
        this.offeringSections = new ArrayList<>();
        this.courseOfferingId = nextId++;
        this.instructors = instructors;
    }

    public String getInstructors() {
        return instructors;
    }

    public void setInstructors(String instructors) {
        this.instructors = instructors;
    }

    public Semester getSemester() {
        return semester;
    }

    public long getSemesterCode() {
        return semesterCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<OfferingSection> getOfferingSections() {
        return offeringSections;
    }

    public OfferingSection getOfferingSectionByType(String type){
        for(OfferingSection offeringSection : offeringSections){
            if(offeringSection.getType().equals(type)){
                return offeringSection;
            }
        }
        return null;
    }

    public void addOfferingSection(OfferingSection offeringSection) {
        offeringSections.add(offeringSection);
    }

    public int getCourseOfferingId() {
        return courseOfferingId;
    }

    public void addWatcher(Watcher watcher){
        watchers.add(watcher);
    }
}
