package ca.courseplanner.model;

/**
 * Model class to decode semester and represent it
 * in different ways
 */
public class Semester {
    private final String term;
    private final int year;

    public Semester(long semester) {
        this.year = calculateYear(String.valueOf(semester));
        this.term = calculateTerm(String.valueOf(semester));
    }


    public String getTerm() {
        return term;
    }

    public int getYear() {
        return year;
    }

    private String calculateTerm(String semester) {
        char termCode = semester.charAt(3);
        switch (termCode) {
            case '1':
                return "Spring";
            case '4':
                return "Summer";
            case '7':
                return "Fall";
            default:
                return "Unknown";
        }
    }

    private int calculateYear(String semester) {
        int X = Character.getNumericValue(semester.charAt(0));
        int Y = Character.getNumericValue(semester.charAt(1));
        int Z = Character.getNumericValue(semester.charAt(2));
        return 1900 + 100 * X + 10 * Y + Z;
    }
}
