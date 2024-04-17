package ca.courseplanner.model;

/**
 * Model class representing offering sections contained inside a
 * course offering
 * Represents a component of an offering
 */

public class OfferingSection {
    private String type;
    private int enrollmentCap;
    private int enrollmentTotal;

    public OfferingSection(String type, int enrollmentCap, int enrollmentTotal) {
        this.type = type;
        this.enrollmentCap = enrollmentCap;
        this.enrollmentTotal = enrollmentTotal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEnrollmentCapacity() {
        return enrollmentCap;
    }

    public void setEnrollmentCapacity(int enrollmentCap) {
        this.enrollmentCap = enrollmentCap;
    }

    public int getEnrollmentTotal() {
        return enrollmentTotal;
    }

    public void setEnrollmentTotal(int enrollmentTotal) {
        this.enrollmentTotal = enrollmentTotal;
    }

}
