package com.springboot.spring_intro.service;

import com.springboot.spring_intro.dto.*;
import com.springboot.spring_intro.entity.Address;
import com.springboot.spring_intro.entity.Course;
import com.springboot.spring_intro.entity.Department;
import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.exception.DuplicateEmailException;
import com.springboot.spring_intro.exception.ResourceNotFoundException;
import com.springboot.spring_intro.exception.StudentNotFoundException;
import com.springboot.spring_intro.projection.StudentProjection;
import com.springboot.spring_intro.repository.CourseRepo;
import com.springboot.spring_intro.repository.DepartmentRepo;
import com.springboot.spring_intro.repository.StudentRepo;

import com.springboot.spring_intro.specification.StudentSpecification;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepo studentRepo;
    private final DepartmentRepo departmentRepo;
    private final CourseRepo courseRepo;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Caching(evict = {
            @CacheEvict(value = "all_students", allEntries = true),
            @CacheEvict(value = "course_count", allEntries = true)
    })
    //@Transactional // unChecked Exception
    @Transactional(rollbackFor = Exception.class)
    @Override
    public StudentResDto saveStudent(StudentReqDto dto) {

        if(studentRepo.existsByEmail(dto.getEmail())){
            throw new DuplicateEmailException("Student Already Exists with email: "+dto.getEmail());
        }

        Department department = departmentRepo.findById(dto.getDepartmentId())
                .orElseThrow(()-> new ResourceNotFoundException("Department Not Found with Id: "+dto.getDepartmentId()));

        // Course Entity
        List<Course> courses = courseRepo.findAllById(dto.getCourseId());

        // Address Entity
        Address address = new Address();
        address.setCity(dto.getAddress().getCity());
        address.setState(dto.getAddress().getState());
        address.setCountry(dto.getAddress().getCountry());

        // Student Entity
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
//        student.setProfileImage(dto.getProfileImage());
//        student.setCourses(dto.getCourses());
        student.setPassword(
                passwordEncoder.encode(dto.getPassword()));
//        student.setCreatedAt(LocalDateTime.now());

        student.setAddress(address);
        student.setDepartment(department);
        student.setCourses(courses);

        Student savedStudent  = studentRepo.save(student);
//        try {
//            sendConfirmationEmail();
//        } catch (Exception e){
//            log.error("Failed to Send Confirmation Email: "+e);
//        }
        return mapToResDto(savedStudent);
    }

    private void sendConfirmationEmail(){
        throw new RuntimeException("Something went Wromg");
    }



    @Cacheable(value = "all_students")
    @Override
    public List<StudentResDto> getAllStudents() {
        log.info("======Fetching all Students======");
        List<StudentResDto> student = studentRepo.findAllWithDetails()
                .stream()
                .map(this::mapToResDto)
                .collect(Collectors.toList());
        log.info("Succesfully Fetched {} Students",student.size());
        return student;
    }

    @Cacheable(value = "students", key = "#id")
    @Override
    public StudentResDto getStudentById(Long id) {
        log.debug("Fetching Student with Id: {}"+id);
        Student student = studentRepo.findById(id)
                .orElseThrow(()-> {
                log.warn("Student not Found with Id: {}"+id);
                return new StudentNotFoundException("Student Not Found with ID: "+id);
                });
        log.info("Student fetched Succesfully with Id: {}"+id);
        return mapToResDto(student);
    }

    @CacheEvict(value = "students", key = "#id")
    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(()-> new StudentNotFoundException("Student not found wiht id: "+id)
        );
        student.setDeleted(true);
        studentRepo.save(student);
    }


    @Override
    public void deleteStudentByEmail(String email) {
        Student student = studentRepo.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Student Not Found with Email: "+ email));
        student.setDeleted(true);
        studentRepo.save(student);
    }

    @CachePut(value = "students", key = "#id")
    @Override
    public StudentResDto updateStudent(Long id, StudentReqDto dto) {

        Student existingStudent = studentRepo.findById(id)
                .orElseThrow(()-> new StudentNotFoundException("Student not Found with ID: "+id));

        // Optimistic Lock
        if (!existingStudent.getVersion().equals(dto.getVersion())){
            throw new OptimisticLockException("Student wad already updated By another user");
        }

        // Duplicate Email Check
        if(studentRepo.existsByEmail(dto.getEmail()) && !existingStudent.getEmail().equals(dto.getEmail())){
            throw new DuplicateEmailException("Student Already Exist with Email: "+dto.getEmail());
        }

            existingStudent.setFirstName(dto.getFirstName());
            existingStudent.setLastName(dto.getLastName());
            existingStudent.setEmail(dto.getEmail());
            existingStudent.setPassword(passwordEncoder.encode(dto.getPassword()));

            Address address = existingStudent.getAddress();
            if(address == null){
                address = new Address();
            }
            if (dto.getAddress() != null) {
                address.setCity(dto.getAddress().getCity());
                address.setState(dto.getAddress().getState());
                address.setCountry(dto.getAddress().getCountry());
            }
            existingStudent.setAddress(address);

            Department department = departmentRepo.findById(dto.getDepartmentId())
                    .orElseThrow(()-> new ResourceNotFoundException("Department Not Found with Id: "+dto.getDepartmentId()));
            existingStudent.setDepartment(department);

            if (dto.getCourseId() != null && !dto.getCourseId().isEmpty()) {
                List<Course> courses = courseRepo.findAllById(dto.getCourseId());
                existingStudent.setCourses(courses);
            }

            Student updateStudent = studentRepo.save(existingStudent);
            List<Course> courses = courseRepo.findAllById(dto.getCourseId());
            existingStudent.setCourses(courses);

            return mapToResDto(updateStudent);

    }

    @Override
    public StudentResDto patchStudent(Long id, StudentReqDto dto) {

        Student existingStudent = studentRepo.findById(id)
                .orElseThrow(()-> new StudentNotFoundException("Student not Found with ID: "+id));

        if (dto.getFirstName() != null) {
            existingStudent.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            existingStudent.setLastName(dto.getLastName());
        }
        if(dto.getEmail() != null){
            if(studentRepo.existsByEmail(dto.getEmail()) && !existingStudent.getEmail().equals(dto.getEmail())){
                throw new DuplicateEmailException("Student already exist with email: "+dto.getEmail());
            }
            existingStudent.setEmail(dto.getEmail());
        }
        if(dto.getPassword() != null){
            existingStudent.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getAddress()!=null){
            Address address = existingStudent.getAddress();

            if(address==null){
                address= new Address();
            }
            if(dto.getAddress().getCity()!=null){
                address.setCity(dto.getAddress().getCity());
            }
            if(dto.getAddress().getState()!=null){
                address.setState(dto.getAddress().getState());
            }
            if(dto.getAddress().getCountry()!=null){
                address.setCountry(dto.getAddress().getCountry());
            }
            existingStudent.setAddress(address);
        }

        if(dto.getDepartmentId()!=null){
            Department department = departmentRepo.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not Found with Id: "+dto.getDepartmentId()));
            existingStudent.setDepartment(department);
        }

        if (dto.getCourseId() != null && !dto.getCourseId().isEmpty()) {
            List<Course> courses = courseRepo.findAllById(dto.getCourseId());
            existingStudent.setCourses(courses);
        }

        Student updatedStudent = studentRepo.save(existingStudent);
        return mapToResDto(updatedStudent);
    }

    @Cacheable(value = "students_by_email", key = "#email")
    @Override
    public StudentResDto getStudentByEmail(String email) {
        Student student = studentRepo.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Student Not Found With Email: "+email));
        return mapToResDto(student);
    }

    @Cacheable(value = "course_count", key = "#courseName")
    @Override
    public Long getStudentCountByCourse(String courseName) {
        return studentRepo.countStudentByCourse(courseName);
    }

    @Override
    public List<StudentResDto> getStudentByFirstNameAndCourse(String firstName, String course) {
        List<Student> students = studentRepo.findByFirstNameAndCourse(firstName, course);
        return students.stream()
                .map(this::mapToResDto)
                .toList();
    }

    @Override
    public List<StudentResDto> findByFirstNameContaining(String keyword) {
        List<Student> students = studentRepo.findByFirstNameContaining(keyword);
        return students.stream()
                .map(this::mapToResDto)
                .toList();
    }

    @Override
    public StudentResDto getStudentByEmailNative(String email) {
        Student student = studentRepo.findStudentByEmailNative(email)
                .orElseThrow(()->
                        new RuntimeException("Student not Found With Email: "+email));
        return mapToResDto(student);
    }

    @Override
    public List<StudentResDto> getAllStudentSortedByFirstName() {
//                List<Student> students = studentRepo.findAll(
////                Sort.by("firstName").descending()
//                // alternate
//                Sort.by(
//                        Sort.Direction.DESC,
//                        "firstName"
//                )
//        );

//        Sort sort = Sort.by("firstName").ascending()
//                .and(Sort.by("lastName").descending());

        Sort sort = Sort.by("firstName")
                .and(Sort.by("lastName"));
        List<Student> students = studentRepo.findAll(sort);

        return students.stream()
                .map(this::mapToResDto)
                .toList();
    }

//    @Override
//    public Page<StudentResDto> getStudent(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").descending());
//        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName"));
//        Page<Student> studentPage = studentRepo.findAll(pageable);
//        return studentPage.map(this::mapToResDto);
//    }

    @Override
    public Page<StudentResDto> getStudent(Pageable pageable) {
        Page<Student> studentPage = studentRepo.findAll(pageable);
        return studentPage.map(this::mapToResDto);
    }

    @Override
    public List<StudentProjectionDto> getStudentProjections() {
        return studentRepo.getStudentProjection();
    }

    @Override
    public List<StudentResDto> searchStudent(String firstName, String lastName, String email, String courses) {
//        Specification<Student> specification =Specification.where(
//                StudentSpecification.hasFirstName(firstName)
//                .and(StudentSpecification.hasLastName(lastName))
//                .and(StudentSpecification.hasEmail(email))
//                .and(StudentSpecification.hasCourse(course)));

        Specification<Student> specification = Specification.allOf(
                StudentSpecification.hasFirstName(firstName),
                StudentSpecification.hasLastName(lastName),
                StudentSpecification.hasEmail(email),
                StudentSpecification.hasCourses(courses));

        List<Student> students = studentRepo.findAll(specification);
        return students.stream()
                .map(this::mapToResDto)
                .toList();
    }

    @Override
    public List<StudentResDto> getAllStudentCustom() {
        List<Student> students = studentRepo.findStudentCustom();
        return students.stream()
                .map(this::mapToResDto)
                .toList();
    }

    @Override
    public List<StudentResDto> getStudentByDepartmentCustom(String departmentName) {
        List<Student> students = studentRepo.findStudentByDepartment(departmentName);
        return students.stream()
                .map(this::mapToResDto).toList();
    }

    @Override
    public StudentResDto uploadProfileImage(Long id, MultipartFile file) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student not Found with Id: "+id));
        if (file.isEmpty()){
            throw new IllegalArgumentException("File Can't be Empty");
        }

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)){
            throw new IllegalArgumentException("Only JPG and PNG Image are Allowed");
        }

        if (file.getSize()>5*1024*1024){
            throw new IllegalArgumentException("File Size Must be less than 5 MB");
        }

        try {
            String fileName = fileStorageService.storeFile(file);
            student.setProfileImage(fileName);
            Student updateStudent = studentRepo.save(student);
            return mapToResDto(updateStudent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to Store Profile Image", e);
        }
    }

    @Override
    public Resource downloadProfileImage(Long id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(()-> new StudentNotFoundException("Student not Found with Id: "+id));

        String fileName = student.getProfileImage();

        if (fileName==null || fileName.isBlank()){
            throw new ResourceNotFoundException("Profile image not Found for student with ID: " + id);
        }
        try {
            return fileStorageService.loadFileAsResource(fileName);
        }catch (IOException e){
            throw new ResourceNotFoundException("Profile Image Not Found: "+fileName);
        }

    }

    private StudentResDto mapToResDto(Student student){
        StudentResDto dto = new StudentResDto();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setProfileImage(dto.getProfileImage());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());
        dto.setCreatedBy(student.getCreatedBy());
        dto.setLastModifiedBy(student.getLastModifiedBy());

//        AddressResponseDTO addressResponseDTO = new AddressResponseDTO();
//        addressResponseDTO.setCity(student.getAddress().getCity());
//        addressResponseDTO.setState(student.getAddress().getState());
//        addressResponseDTO.setCountry(student.getAddress().getCountry());
//        dto.setAddress(addressResponseDTO);

        if (student.getAddress() != null) {
            AddressResponseDTO addressResponseDTO = new AddressResponseDTO();
            addressResponseDTO.setCity(student.getAddress().getCity());
            addressResponseDTO.setState(student.getAddress().getState());
            addressResponseDTO.setCountry(student.getAddress().getCountry());
            dto.setAddress(addressResponseDTO);
        }

        dto.setDepartmentName(student.getDepartment().getDepartmentName());

        dto.setCourses(
                student.getCourses()
                        .stream()
                        .map(course -> new
                                CourseResDto(
                                        course.getId(),
                                course.getCourseName(),
                                course.getDuration(),
                                course.getFees(),
                                course.getInstructorName()
                        )).toList()
        );
        dto.setVersion(student.getVersion());

        return dto;
    }

}
