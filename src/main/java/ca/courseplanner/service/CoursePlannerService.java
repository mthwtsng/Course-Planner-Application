package ca.courseplanner.service;

import ca.courseplanner.dto.*;
import ca.courseplanner.model.*;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class to handle course planning related logic and operations
 * Manages Departments, Courses, CourseOfferings and SectionOfferings
 * Provides method
 */

@Service
public class CoursePlannerService {
    private final List<Department> departments = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Methods related to adding course and grouping them
    public void addCourseData(OfferingData offeringData) {
        String subject = offeringData.getSubject();
        String catalogNumber = offeringData.getCatalogNumber();
        long semesterCode = offeringData.getSemesterCode();
        String location = offeringData.getLocation();
        String instructors = offeringData.getInstructors();
        String componentCode = offeringData.getComponentCode();
        int enrollmentCap = offeringData.getEnrolmentCapacity();
        int enrollmentTotal = offeringData.getEnrolmentTotal();

        Department department = findOrCreateDepartment(subject);
        Course course = findOrCreateCourse(department, subject, catalogNumber);

        CourseOffering courseOffering = new CourseOffering(semesterCode, location, instructors);
        OfferingSection offeringSection = new OfferingSection(componentCode, enrollmentCap, enrollmentTotal);
        addOfferingToCourse(course, courseOffering, offeringSection);
        notifyWatchers(course, componentCode, semesterCode, enrollmentTotal, enrollmentCap);
    }

    private Department findOrCreateDepartment(String subject) {
        Department department = getDepartmentByName(subject);
        if (department == null) {
            department = new Department(subject);
            departments.add(department);
        }
        return department;
    }

    private Course findOrCreateCourse(Department department, String subject, String catalogNumber) {
        Course existingCourse = department.getCourseBySubjectAndCatalogNumber(subject, catalogNumber);
        if (existingCourse == null) {
            Course newCourse = new Course(subject, catalogNumber);
            department.addCourse(newCourse);
            return newCourse;
        }
        return existingCourse;
    }


    private void addOfferingToCourse(Course course, CourseOffering courseOffering, OfferingSection offeringSection) {
        CourseOffering existingOffering = course.getSameCourseOffering(courseOffering);
        if (existingOffering != null) {
            updateOfferingSections(existingOffering, offeringSection);
        } else {
            course.addCourseOffering(courseOffering);
            updateOfferingSections(courseOffering, offeringSection);
        }
    }

    private void updateOfferingSections(CourseOffering courseOffering, OfferingSection offeringSection) {
        OfferingSection existingSection = courseOffering.getOfferingSectionByType(offeringSection.getType());
        if (existingSection != null) {
            existingSection.setEnrollmentCapacity(existingSection.getEnrollmentCapacity() + offeringSection.getEnrollmentCapacity());
            existingSection.setEnrollmentTotal(existingSection.getEnrollmentTotal() + offeringSection.getEnrollmentTotal());
        } else {
            courseOffering.addOfferingSection(offeringSection);
        }
    }

    private void notifyWatchers(Course course, String componentCode, long semesterCode, int enrollmentTotal, int enrollmentCap) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
        String formattedDateTime = now.format(formatter);
        String message = formattedDateTime + ": Added section " + componentCode + " with enrollment ("
                + enrollmentTotal + "/" + enrollmentCap + ") to offering " + semesterCode;
        course.notifyWatchers(message);
    }

    // Method to handle graph logic
    public List<ApiGraphDataPointDTO> getStudentsPerSemester(long departmentId) {
        Department department = getDepartmentById(departmentId);
        if (department == null) {
            return null;
        }
        List<ApiGraphDataPointDTO> graphDataPoints = new ArrayList<>();
        List<Long> uniqueSemesterCodes = findUniqueSemesterCodes(department);

        for (Long semesterCode : uniqueSemesterCodes) {
            int totalSeatsTaken = getTotalSeatsTakenForSemester(department, semesterCode);
            ApiGraphDataPointDTO dataPoint = new ApiGraphDataPointDTO();
            dataPoint.semesterCode = semesterCode;
            dataPoint.totalCoursesTaken = totalSeatsTaken;
            graphDataPoints.add(dataPoint);
        }

        return graphDataPoints;
    }

    private List<Long> findUniqueSemesterCodes(Department department) {
        return department.getCourses().stream()
                .flatMap(course -> course.getAllCourseOfferings().stream())
                .filter(courseOffering -> courseOffering.getOfferingSections().stream()
                        .anyMatch(offeringSection -> offeringSection.getType().equals("LEC")))
                .map(CourseOffering::getSemesterCode)
                .distinct()
                .collect(Collectors.toList());
    }

    private int getTotalSeatsTakenForSemester(Department department, Long semesterCode) {
        return department.getCourses().stream()
                .flatMap(course -> course.getAllCourseOfferings().stream())
                .filter(courseOffering -> courseOffering.getSemesterCode() == semesterCode)
                .flatMap(courseOffering -> courseOffering.getOfferingSections().stream())
                .filter(offeringSection -> "LEC".equals(offeringSection.getType()))
                .mapToInt(OfferingSection::getEnrollmentTotal)
                .sum();
    }


    // ----------------------------------------------------------------------------------------------
    // Methods related to handling watchers
    public void createNewWatcher(long deptId, long courseId) {
        Department department = getDepartmentById(deptId);
        Course course = department.getCourseById(courseId);
        if (department != null && course != null) {
            Watcher newWatcher = new Watcher(department, course);
        } else {
            throw new IllegalArgumentException("Department or course not found");
        }
    }

    public List<ApiWatcherDTO> getAllWatchers() {
        List<ApiWatcherDTO> allWatchers = new ArrayList<>();
        for (Department department : departments) {
            for (Course course : department.getCourses()) {
                for (Watcher watcher : course.getAllWatchers()) {
                    ApiWatcherDTO watcherDTO = new ApiWatcherDTO();
                    watcherDTO.id = (watcher.getId());
                    watcherDTO.department = departmentToDto(department);
                    watcherDTO.course = courseToDto(course);
                    watcherDTO.events = watcher.getEvents();
                    allWatchers.add(watcherDTO);
                }
            }
        }
        return allWatchers;
    }

    public void deleteWatcher(Watcher watcher){
        for (Department department : departments) {
            for (Course course : department.getCourses()) {
                course.removeWatcher(watcher);
            }
        }
    }

    public Watcher getWatcherById(int watcherId){
        for (Department department : departments) {
            for (Course course : department.getCourses()) {
                for(Watcher watcher : course.getAllWatchers()){
                    if(watcher.getId() == watcherId){
                        return watcher;
                    }
                }
            }
        }
        return null;
    }

    // -----------------------------------------------------
    // Methods for handling dumping of model
    public void dumpModel() {
        for (Department department : departments) {
            for (Course course : department.getCourses()) {
                System.out.println(course.getSubject() + " " + course.getCatalogNumber());
                printCourseOfferings(course);
            }
        }
    }
    private void printCourseOfferings(Course course) {
        int i = 0;
        for(CourseOffering courseOffering : course.getAllCourseOfferings()){
            System.out.println("   " + courseOffering.getSemesterCode() + " in " + courseOffering.getLocation() + " by " + courseOffering.getInstructors());
            for(OfferingSection offeringSection : courseOffering.getOfferingSections()){
                printOfferingSection(offeringSection.getType(), offeringSection.getEnrollmentTotal(), offeringSection.getEnrollmentCapacity());
            }
        }
    }
    private void printOfferingSection(String currentType, int totalEnrollment, int totalCapacity) {
        System.out.println("        Type = " + currentType + ", Enrollment = " +
                totalEnrollment + "/" + totalCapacity);
    }


    // -----------------------------------------------------------------------
    // Methods for retrieving departments, courses and courseOfferings
    public List<Department> getAllDepartments() {
        return departments;
    }
    private Department getDepartmentByName(String name) {
        for (Department department : departments) {
            if (department.getDepartmentName().equals(name)) {
                return department;
            }
        }
        return null;
    }
    public Department getDepartmentById(long deptId) {
        for (Department department : departments) {
            if (department.getDepartmentId() == deptId) {
                return department;
            }
        }
        return null;
    }
    public CourseOffering getCourseOfferingById(Course course, int offeringId) {
        return course.getCourseOfferingById(offeringId);
    }
    public Course getCourseById(Department department, long courseId) {
        return department.getCourseById(courseId);
    }


    // -----------------------------------------------------------------------
    // Methods for handling models to Dto
    public List<ApiDepartmentDTO> getAllDepartmentsAsDTO(){
        List<ApiDepartmentDTO> departmentDTOS = new ArrayList<>();
        for(Department department : departments){
            ApiDepartmentDTO departmentDTO = departmentToDto(department);
            departmentDTOS.add(departmentDTO);
        }
        return departmentDTOS;
    }

    public List<ApiCourseDTO> getCoursesDtoByDepartment(Department department){
        List<ApiCourseDTO> coursesDTO = new ArrayList<>();
        List<Course> courses = department.getCourses();
        for(Course course : courses){
            ApiCourseDTO courseDTO = courseToDto(course);
            coursesDTO.add(courseDTO);
        }
        return coursesDTO;
    }

    public List<ApiCourseOfferingDTO> getCourseOfferingsAsDto(Course course, int courseId) {
        if (course != null) {
            return courseOfferingToDTOS(course.getAllCourseOfferings());
        }
        return null;
    }

    public List<ApiOfferingSectionDTO> getOfferingSectionAsDto(Department department, Course course, CourseOffering offering) {
        if (department != null && course != null && offering != null) {
            List<ApiOfferingSectionDTO> offeringDetailsList = new ArrayList<>();
            for (OfferingSection section : offering.getOfferingSections()) {
                ApiOfferingSectionDTO offeringSectionDTO = new ApiOfferingSectionDTO();
                offeringSectionDTO.type = section.getType();
                offeringSectionDTO.enrollmentCap = section.getEnrollmentCapacity();
                offeringSectionDTO.enrollmentTotal = section.getEnrollmentTotal();
                offeringDetailsList.add(offeringSectionDTO);
            }
            return offeringDetailsList;
        }
        return null;
    }

    private ApiDepartmentDTO departmentToDto(Department department){
        ApiDepartmentDTO departmentDTO = new ApiDepartmentDTO();
        departmentDTO.name = department.getDepartmentName();
        departmentDTO.deptId = department.getDepartmentId();
        return departmentDTO;
    }

    private ApiCourseDTO courseToDto(Course course){
        ApiCourseDTO courseDTO = new ApiCourseDTO();
        courseDTO.courseId = course.getCourseId();
        courseDTO.catalogNumber = course.getCatalogNumber();
        return courseDTO;
    }

    private List<ApiCourseOfferingDTO> courseOfferingToDTOS(List<CourseOffering> courseOfferings){
        List<ApiCourseOfferingDTO> courseOfferingDTOS = new ArrayList<>();
        for(CourseOffering courseOffering : courseOfferings){
            ApiCourseOfferingDTO courseOfferingDTO = new ApiCourseOfferingDTO();
            courseOfferingDTO.courseOfferingId = courseOffering.getCourseOfferingId();
            courseOfferingDTO.instructors = courseOffering.getInstructors();
            courseOfferingDTO.location = courseOffering.getLocation();
            courseOfferingDTO.term  = (courseOffering.getSemester()).getTerm();
            courseOfferingDTO.year = (courseOffering.getSemester()).getYear();
            courseOfferingDTO.semesterCode = courseOffering.getSemesterCode();
            courseOfferingDTOS.add(courseOfferingDTO);
        }
        return courseOfferingDTOS;
    }

    public void OfferingDataDTOtoModel(ApiOfferingDataDTO offeringDTO) {
        OfferingData offeringData = new OfferingData(
                offeringDTO.subjectName,
                offeringDTO.catalogNumber,
                offeringDTO.semester,
                offeringDTO.location,
                offeringDTO.enrollmentCap,
                offeringDTO.enrollmentTotal,
                offeringDTO.instructor,
                offeringDTO.component
        );
        addCourseData(offeringData);
    }
}