package ca.courseplanner.model;

/**
 * Model Class representing entire data of one course offering
 * Used to load CSV File data before being processed into specific
 * classes
 */

public class OfferingData {
    private static int nextId = 1;
    private Semester semester;
    private long semesterCode;
    private String location;
    private int enrolmentCapacity;
    private int enrolmentTotal;
    private String instructors;
    private String componentCode;
    private int courseOfferingId;
    private String subject;
    private String catalogNumber;

    public OfferingData(String subject, String catalogNumber, String semester, String location, int enrolmentCapacity,
                        int enrolmentTotal, String instructors, String componentCode) {
        this.semester = new Semester(Long.parseLong(semester));
        this.semesterCode = Long.parseLong(semester);
        this.location = location;
        this.enrolmentCapacity = enrolmentCapacity;
        this.enrolmentTotal = enrolmentTotal;
        this.instructors = instructors;
        this.componentCode = componentCode;
        this.courseOfferingId = nextId++;
        this.subject = subject;
        this.catalogNumber = catalogNumber;
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

    public Semester getSemester() {
        return semester;
    }

    public long getSemesterCode(){
        return semesterCode;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEnrolmentCapacity() {
        return enrolmentCapacity;
    }

    public void setEnrolmentCapacity(int enrolmentCapacity) {
        this.enrolmentCapacity = enrolmentCapacity;
    }

    public int getEnrolmentTotal() {
        return enrolmentTotal;
    }

    public void setEnrolmentTotal(int enrolmentTotal) {
        this.enrolmentTotal = enrolmentTotal;
    }

    public String getInstructors() {
        return instructors;
    }

    public void setInstructors(String instructors) {
        this.instructors = instructors;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }
    public int getCourseOfferingId(){
        return courseOfferingId;
    }

    @Override
    public String toString() {
        return "UngroupedCourseOffering{" +
                "semester=" + semester +
                ", semesterCode=" + semesterCode +
                ", location='" + location + '\'' +
                ", enrolmentCapacity=" + enrolmentCapacity +
                ", enrolmentTotal=" + enrolmentTotal +
                ", instructors='" + instructors + '\'' +
                ", componentCode='" + componentCode + '\'' +
                ", courseOfferingId=" + courseOfferingId +
                ", subject='" + subject + '\'' +
                ", catalogNumber='" + catalogNumber + '\'' +
                '}';
    }


}
