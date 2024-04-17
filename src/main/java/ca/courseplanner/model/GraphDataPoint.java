package ca.courseplanner.model;

public class GraphDataPoint {
    private long semesterCode;
    private long totalCoursesTaken;

    public GraphDataPoint(long semesterCode, long totalCoursesTaken) {
        this.semesterCode = semesterCode;
        this.totalCoursesTaken = totalCoursesTaken;
    }

    public long getSemesterCode() {
        return semesterCode;
    }

    public void setSemesterCode(long semesterCode) {
        this.semesterCode = semesterCode;
    }

    public long getTotalCoursesTaken() {
        return totalCoursesTaken;
    }

    public void setTotalCoursesTaken(long totalCoursesTaken) {
        this.totalCoursesTaken = totalCoursesTaken;
    }
}
