package com.springboot.spring_intro.controller;

import com.springboot.spring_intro.dto.StudentProjectionDto;
import com.springboot.spring_intro.dto.StudentReqDto;
import com.springboot.spring_intro.dto.StudentResDto;
import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.payload.ApiResponse;
import com.springboot.spring_intro.projection.StudentProjection;
import com.springboot.spring_intro.repository.StudentRepo;
import com.springboot.spring_intro.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(
        name = "Student APIs",
        description = "APIs for Managing Students"
)
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(
            summary = "Get All Students",
            description = "Retrieves All Active Students"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student Retrieved Successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No Student Found"
            )
    })

    // http://localhost:8080/student
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<StudentResDto>>> getAllStudent() {

        List<StudentResDto> student = studentService.getAllStudents();

        ApiResponse<List<StudentResDto>> response = new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Student Fetched Successfully",
                student
        );
        return ResponseEntity.ok(response);
    }

    // Get Student by ID
    @Operation(
            summary = "Get Student by ID",
            description = "Retrieves a Student using the student ID"
    )
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<StudentResDto>> getStudentById(@Parameter(description = "Student ID",
    required = true) @PathVariable Long id) {

        StudentResDto student = studentService.getStudentById(id);

        ApiResponse<StudentResDto> response = new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Student Fetched Succesfully",
                student
        );

        return ResponseEntity.ok(response);

    }

    // Save Student
    @Operation(
            summary = "Create a New Student",
            description = "Creates a new Student"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResDto>> saveStudent(@Valid
                                                                  @RequestBody StudentReqDto dto) {

        StudentResDto savedStd = studentService.saveStudent(dto);

        ApiResponse<StudentResDto> response = new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.CREATED.value(),
                "Student Created Successfully",
                savedStd
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    @Operation(
            summary = "Update Student",
            description = "Updates a Student using Student ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResDto>> updateStudent(@PathVariable Long id,
                                                                    @Valid
                                                                    @RequestBody StudentReqDto dto) {
        StudentResDto updateStudent = studentService.updateStudent(id, dto);

        ApiResponse<StudentResDto> response = new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Student Updated Successfully",
                updateStudent
        );

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Partially Update Student",
            description = "Updates Selected Fields of Existing Student"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResDto>> patchStudent(@PathVariable Long id,
                                                                   @RequestBody StudentReqDto dto) {

        StudentResDto patchStudent = studentService.patchStudent(id, dto);

        ApiResponse<StudentResDto> response = new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "",
                patchStudent
        );

//        return ResponseEntity.ok(
//                new ApiResponse<>(
//                        true,
//                        LocalDateTime.now(),
//                        HttpStatus.OK.value(),
//                        "",
//                        studentService.patchStudent(id, dto)
//                )
//        );

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Delete Student",
            description = "Deletes a Student Using the Student ID"
    )
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStudent(@PathVariable Long id) {

        studentService.deleteStudent(id);

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Student Deleted Successfully",
                null //
        );

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Get Student By Email",
            description = "Retrieves a Student using the Email Address"
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<StudentResDto> getStudentByEmail(@PathVariable String email) {
        return ResponseEntity.ok(
                studentService.getStudentByEmail(email)
        );
    }

    @Operation(
            summary = "Get Student Count By Course",
            description = "Returns the Number of Students Enrolled in a Course"
    )
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getStudentCountByCourse(@RequestParam String courseName) {
        Long count = studentService.getStudentCountByCourse(courseName);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Student Count Fetched",
                        count
                )
        );
    }

    @Operation(
            summary = "Delete Student By Email",
            description = "Delete a Student Using the Email Address"
    )
    @DeleteMapping("/email/{email}")
    public ResponseEntity<ApiResponse<String>> deleteStudentByEmail(@PathVariable String email) {

        studentService.deleteStudentByEmail(email);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Student Deleted Succesfully",
                        null
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentResDto>> getStudent(@RequestParam String firstName,
                                                          @RequestParam String course){

        return ResponseEntity.ok(
                studentService.getStudentByFirstNameAndCourse(firstName, course));
    }

    @GetMapping("/search/key")
    public ResponseEntity<List<StudentResDto>> getStudentByFirstName(@RequestParam String firstName){

        return ResponseEntity.ok(
                studentService.findByFirstNameContaining(firstName));
    }

    @GetMapping("/native/email/{email}")
    public ResponseEntity<StudentResDto> getStudentByEmailNative(@PathVariable String email){
        StudentResDto res = studentService.getStudentByEmailNative(email);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<StudentResDto>> getSortedStudents(){
        return ResponseEntity.ok(studentService.getAllStudentSortedByFirstName());
    }

//    @GetMapping("/page")
//    public ResponseEntity<Page<StudentResDto>> getAllStudents(@RequestParam(defaultValue = "0")int page,
//                                                              @RequestParam(defaultValue = "5") int size){
//        return ResponseEntity.ok(studentService.getStudent(page, size));
//    }

    @GetMapping("/page")
    public ResponseEntity<Page<StudentResDto>> getAllStudents(Pageable pageable){
        return ResponseEntity.ok(studentService.getStudent(pageable));
    }

//    @GetMapping("/projection")
//    public ResponseEntity<List<StudentProjection>> getStudentProjection(){
//        return ResponseEntity.ok(studentService.getStudentProjections());
//    }

    @GetMapping("/projection/dto")
    public List<StudentProjectionDto> getStudentProjection(){
        return studentService.getStudentProjections();
    }

    @GetMapping("/search/specification")
    public ResponseEntity<ApiResponse<List<StudentResDto>>> searchStudent(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String courses
    ){
        List<StudentResDto> students = studentService.searchStudent(firstName, lastName, email, courses);
        ApiResponse<List<StudentResDto>> res = new ApiResponse<>(
                true,
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Student Fetched Succesfully",
                students
        );
        return ResponseEntity.ok(res);
    }

    @GetMapping("/custom")
    public List<StudentResDto> getAllStudentCustom(){
        return studentService.getAllStudentCustom();
    }

    @GetMapping("/custom/departmentName")
    public List<StudentResDto> getStudentByDepartment(@RequestParam String departmentName){
        return studentService.getStudentByDepartmentCustom(departmentName);
    }

    @Operation(summary = "Upload Student Profile Image",
            description = "Upload a JPG or PNG Profile Image For a Student")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile Image Uploaded Successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Invalid File, Unsupported File Type or File Size"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student or Profile Image not Found"
            )
    })

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentResDto> uploadProfileImage(@PathVariable Long id,
                                                            @RequestParam("file")MultipartFile file){
        return ResponseEntity.ok(
                studentService.uploadProfileImage(id, file)
        );
    }

    @Operation(summary = "Download Student Profile Image",
    description = "Download Student Profile Image Using Student ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile Image Downloaded Successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student or Profile Image Not Found"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> doownloadProfileImage(@PathVariable Long id){
        Resource resource = studentService.downloadProfileImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\"" + resource.getFilename()+"\"")
                .body(resource);
    }

}
