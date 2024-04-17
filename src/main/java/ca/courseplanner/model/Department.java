package ca.courseplanner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a department
 * Each department contains 1-to-many courses
 */

public class Department {
    private static int nextId = 1;
    private int departmentId;
    private String departmentName;
    private List<Course> courses = new ArrayList<>();

    public Department(String departmentName) {
        this.departmentName = departmentName;
        this.departmentId = nextId++;
    }
    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public void addCourse(Course course){
        courses.add(course);
    }
    public Course getCourse(String catalogNumber){
        for(Course course : courses){
            if(course.getCatalogNumber().equals(catalogNumber)){
                return course;
            }
        }
        return null;
    }
    public Course getCourseBySubjectAndCatalogNumber(String subject, String catalogNumber){
        for(Course course : courses){
            if ((course.getSubject()).equals(subject) && (course.getCatalogNumber()).equals(catalogNumber)) {
                return course;
            }
        }
        return null;
    }

    public Course getCourseById(long courseId) {
        for (Course course : courses) {
            if (course.getCourseId() == courseId) {
                return course;
            }
        }
        return null;
    }
    public List<Course> getCourses(){
        return courses;
    }
    public int getDepartmentId(){
        return departmentId;
    }
}
