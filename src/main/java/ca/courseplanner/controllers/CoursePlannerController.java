package ca.courseplanner.controllers;

import ca.courseplanner.dto.*;
import ca.courseplanner.model.Course;
import ca.courseplanner.model.CourseOffering;
import ca.courseplanner.model.Department;
import ca.courseplanner.model.Watcher;
import ca.courseplanner.service.CoursePlannerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to handle HTTP requests for course planner
 */

@RestController
@RequestMapping("/api")
public class CoursePlannerController {

    CoursePlannerService coursePlannerService = new CoursePlannerService();

    public CoursePlannerController(CoursePlannerService coursePlannerService) {
        this.coursePlannerService = coursePlannerService;
    }

    @GetMapping
    public List<Department> getAllCourses() {
        return coursePlannerService.getAllDepartments();
    }

    @GetMapping("/about")
    public ApiAboutDTO getAboutInfo() {
        String appName = "SFU COURSE PLANNER";
        String authorName = "MATTHEW TSENG";
        return new ApiAboutDTO(appName, authorName);

    }

    @GetMapping("/dump-model")
    public void dumpModel() {
        coursePlannerService.dumpModel();
    }

    @GetMapping("/departments")
    public List<ApiDepartmentDTO> getDepartments() {
        return coursePlannerService.getAllDepartmentsAsDTO();
    }

    @GetMapping("/departments/{deptId}/courses")
    public ResponseEntity<?> getCoursesByDepartment(@PathVariable int deptId) {
        Department department = coursePlannerService.getDepartmentById(deptId);
        if (department == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department Not Found with Id " + deptId);
        }
        List<ApiCourseDTO> courses = coursePlannerService.getCoursesDtoByDepartment(department);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/departments/{deptId}/courses/{courseId}/offerings")
    public ResponseEntity<?> getCourseOfferings(@PathVariable int deptId, @PathVariable int courseId) {
        Department department = coursePlannerService.getDepartmentById(deptId);
        if (department == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deparment not found with Id  " + deptId);
        }
        Course course = coursePlannerService.getCourseById(department, courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found with Id " + courseId);
        }
        List<ApiCourseOfferingDTO> courseOfferings = coursePlannerService.getCourseOfferingsAsDto(course, courseId);
        return ResponseEntity.ok(courseOfferings);
    }

    @GetMapping("/departments/{deptId}/courses/{courseId}/offerings/{offeringId}")
    public ResponseEntity<?> getOfferingSection(
            @PathVariable int deptId,
            @PathVariable int courseId,
            @PathVariable int offeringId) {
        Department department = coursePlannerService.getDepartmentById(deptId);
        if (department == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found with Id " + deptId);
        }
        Course course = coursePlannerService.getCourseById(department, courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found with Id " + courseId);
        }
        CourseOffering offering = coursePlannerService.getCourseOfferingById(course, offeringId);
        if (offering == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Offering not found with Id " + offeringId);
        }
        List<ApiOfferingSectionDTO> offeringDetails = coursePlannerService.getOfferingSectionAsDto(department, course, offering);
        return ResponseEntity.ok(offeringDetails);
    }

    @PostMapping("/addoffering")
    public void addCourseOffering(@RequestBody ApiOfferingDataDTO newCourseOffering) {
        coursePlannerService.OfferingDataDTOtoModel(newCourseOffering);
    }

    @PostMapping("/watchers")
    public void createWatcher(@RequestBody ApiWatcherCreateDTO watcherCreate) {
        coursePlannerService.createNewWatcher(watcherCreate.deptId, watcherCreate.courseId);
    }

    @GetMapping("/watchers")
    public ResponseEntity<?> getAllWatchers() {
        List<ApiWatcherDTO> watchers;
        try {
            watchers = coursePlannerService.getAllWatchers();
            return ResponseEntity.ok(watchers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to retrieve all watchers");
        }
    }

    @GetMapping("/watchers/{watcherId}")
    public ResponseEntity<?> getEventsForWatcher(@PathVariable int watcherId) {
        Watcher watcher = coursePlannerService.getWatcherById(watcherId);
        if(watcher == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Watcher not found with Id " + watcherId);
        }
        List<String> events = watcher.getEvents();
        return ResponseEntity.ok().body(events);
    }

    @DeleteMapping("/watchers/{watcherId}")
    public ResponseEntity<?> deleteWatcher(@PathVariable int watcherId) {
        Watcher watcher = coursePlannerService.getWatcherById(watcherId);
        if(watcher == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Watcher not found with Id " + watcherId);
        }
        coursePlannerService.deleteWatcher(watcher);
        return ResponseEntity.ok(null);

    }

    @GetMapping("/stats/students-per-semester")
    public ResponseEntity<?> getStudentsPerSemester(@RequestParam long deptId) {
        List<ApiGraphDataPointDTO> studentStats = coursePlannerService.getStudentsPerSemester(deptId);
        if (studentStats != null && !studentStats.isEmpty()) {
            return ResponseEntity.ok(studentStats);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found with Id " + deptId);
        }
    }

}

