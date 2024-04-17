package ca.courseplanner;

import ca.courseplanner.model.OfferingData;
import ca.courseplanner.service.CoursePlannerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Responsible for loading course data from CSV file and populates Service class
 * with data
 */
@Component
public class CourseDataLoaderRunner implements CommandLineRunner {
    private List<OfferingData> offeringDatas = new ArrayList<>();
    private static final String CSV_FILE_PATH = "data/course_data_2018.csv";
    private final CoursePlannerService coursePlannerService;
    public CourseDataLoaderRunner(CoursePlannerService coursePlannerService) {
        this.coursePlannerService = coursePlannerService;
    }

    @Override
    public void run(String... args){
        File file = new File(CSV_FILE_PATH);
        try {
            Scanner scanner = new Scanner(file);
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String csvLine = scanner.nextLine();
                processCSVLine(csvLine);
            }
            sortOfferingData();
            addCourseData();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addCourseData(){
        for(OfferingData offeringData : offeringDatas) {
            coursePlannerService.addCourseData(offeringData);
        }
    }
//    private void groupCourseOfferings() {
//        String currentOfferingInfo = "";
//        CourseOffering currentCourseOffering = null;
//        String subject = "";
//        String catalogNumber = "";
//
//        for (OfferingData offering : offeringData) {
//            String offeringInfo = offering.getSemesterCode() + offering.getLocation() + offering.getInstructors();
//            if (!offeringInfo.equals(currentOfferingInfo)) {
//                currentCourseOffering = createNewCourseOffering(offering, subject, catalogNumber);
//                if (currentCourseOffering != null) {
//                    coursePlannerService.addCourseOffering(subject, catalogNumber, currentCourseOffering);
//                }
//                currentOfferingInfo = offeringInfo;
//                subject = offering.getSubject();
//                catalogNumber = offering.getCatalogNumber();
//            }
//            updateOfferingSection(currentCourseOffering, offering);
//        }
//
//        if (currentCourseOffering != null) {
//            coursePlannerService.addCourseOffering(subject, catalogNumber, currentCourseOffering);
//        }
//    }
//
//    private CourseOffering createNewCourseOffering(OfferingData offering, String subject, String catalogNumber) {
//        return new CourseOffering(
//                offering.getSemesterCode(),
//                offering.getLocation(),
//                offering.getInstructors()
//        );
//    }
//
//    private void updateOfferingSection(CourseOffering currentCourseOffering, OfferingData offering) {
//        OfferingSection existingSection = currentCourseOffering.getOfferingSectionByType(offering.getComponentCode());
//        if (existingSection != null) {
//            existingSection.setEnrollmentCapacity(existingSection.getEnrollmentCapacity() + offering.getEnrolmentCapacity());
//            existingSection.setEnrollmentTotal(existingSection.getEnrollmentTotal() + offering.getEnrolmentTotal());
//        } else {
//            OfferingSection offeringSection = new OfferingSection(
//                    offering.getComponentCode(),
//                    offering.getEnrolmentCapacity(),
//                    offering.getEnrolmentTotal()
//            );
//            currentCourseOffering.addOfferingSection(offeringSection);
//        }
//    }

    private void sortOfferingData() {
        offeringDatas.sort(
                Comparator.comparing(OfferingData::getSubject)
                        .thenComparing(OfferingData::getCatalogNumber)
                        .thenComparing(OfferingData::getSemesterCode)
                        .thenComparing(OfferingData::getLocation)
                        .thenComparing(OfferingData::getInstructors)
                        .thenComparing(OfferingData::getComponentCode)
        );
    }

    private void processCSVLine(String csvLine) {
        List<String> attributes = parseCSVLine(csvLine);
        String semester = attributes.get(0);
        String subject = attributes.get(1);
        String catalogNumber = attributes.get(2);
        String location = attributes.get(3);
        int enrolmentCapacity = Integer.parseInt(attributes.get(4));
        int enrolmentTotal = Integer.parseInt(attributes.get(5));
        String instructors = "";
        if(!(attributes.get(6).equals("(null)") || attributes.get(6).equals("<null>"))) {
            instructors = attributes.get(6);
        }
        String componentCode = attributes.get(7);
        OfferingData offeringData = new OfferingData(subject, catalogNumber, semester, location, enrolmentCapacity,
                enrolmentTotal, instructors, componentCode);
        this.offeringDatas.add(offeringData);
    }

    private List<String> parseCSVLine(String csvLine) {
        List<String> attributes = new ArrayList<>();
        StringBuilder processedString = new StringBuilder();
        boolean betweenQuotations = false;
        for (char character : csvLine.toCharArray()) {
            if (character == '"') {
                betweenQuotations = !betweenQuotations;
            } else if (character == ',' && !betweenQuotations) {
                attributes.add(processedString.toString().trim());
                processedString.setLength(0);
            } else {
                processedString.append(character);
            }
        }
        attributes.add(processedString.toString().trim());

        return attributes;
    }
}
