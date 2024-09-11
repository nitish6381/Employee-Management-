//package employeeManagementSystem.service;
//
//import employeeManagementSystem.dto.DepartmentDTO;
//import employeeManagementSystem.dto.DepartmentResponseDTO;
//import employeeManagementSystem.dto.EmployeeResponseDTO;
//import employeeManagementSystem.dto.ResponseDTO;
//import employeeManagementSystem.model.Department;
//import employeeManagementSystem.model.Employee;
//import employeeManagementSystem.repository.DepartmentRepository;
//import employeeManagementSystem.repository.EmployeeRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class DepartmentService {
//    @Autowired
//    private DepartmentRepository departmentRepository;
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    public ResponseEntity<ResponseDTO> createDepartment(DepartmentDTO departmentDTO){
//        List<Department> departments = departmentRepository.findByName(departmentDTO.getName().toLowerCase());
//        if (!departments.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDTO(false, "Department already exists", null));
//        }
//        Department department = new Department();
//        department.setName(departmentDTO.getName());
//        department.setLocation(departmentDTO.getLocation());
//        if(departmentRepository.save(department) != null) {
//            return ResponseEntity.ok(new ResponseDTO(true, "Department created successfully", getDepartmentResponseDTO(department)));
//        } else {
//            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Department creation failed", null));
//        }
//    }
//
//    public ResponseEntity<ResponseDTO> getAllDepartments() {
//        List<Department> departments = departmentRepository.findAll();
//        List<DepartmentResponseDTO> departmentResponseDTOS = departments.stream().map(this::getDepartmentResponseDTO).toList();
//        return ResponseEntity.ok(new ResponseDTO(true, "Employees fetched successfully", departmentResponseDTOS));
//    }
//
//    public Optional<Department> getDepartmentById(UUID id) {
//        return departmentRepository.findById(id);
//    }
//
//    public Department createOrUpdateDepartment(Department department) {
//        return departmentRepository.save(department);
//    }
//
//    public void deleteDepartment(UUID id) {
//        departmentRepository.deleteById(id);
//    }
//
//    public ResponseEntity<ResponseDTO> getEmployeesInDepartment(UUID departmentId) {
//        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);
//        List<EmployeeResponseDTO> employeeResponseDTOS = employees.stream()
//                .map(employee -> getEmployeeResponseDTO(employee))
//                .toList();
//        return ResponseEntity.ok(new ResponseDTO(true, "Employees fetched successfully", employeeResponseDTOS));
//    }
//
//    public Employee addEmployeeToDepartment(UUID departmentId, Employee employee) {
//        Department department = departmentRepository.findById(departmentId)
//                .orElseThrow(() -> new RuntimeException("Department not found"));
//
//        employee.setDepartment(department);
//        department.getEmployees().add(employee);
//        departmentRepository.save(department);
//        return employee;
//    }
//
//    public void deleteEmployeeFromDepartment(UUID departmentId, String employeeId) {
//        Department department = departmentRepository.findById(departmentId)
//                .orElseThrow(() -> new RuntimeException("Department not found"));
//
//        department.getEmployees().removeIf(employee -> employee.getId().equals(employeeId));
//        departmentRepository.save(department);
//    }
//
//    public DepartmentResponseDTO getDepartmentResponseDTO(Department department) {
//        DepartmentResponseDTO departmentResponseDTO = new DepartmentResponseDTO();
//        departmentResponseDTO.setId(department.getId());
//        departmentResponseDTO.setName(department.getName());
//        departmentResponseDTO.setLocation(department.getLocation());
//        return departmentResponseDTO;
//    }
//
//    public EmployeeResponseDTO getEmployeeResponseDTO(Employee employee) {
//        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();
//        employeeResponseDTO.setId(employee.getId());
//        employeeResponseDTO.setName(employee.getName());
//        employeeResponseDTO.setEmail(employee.getEmail());
//        employeeResponseDTO.setSalary(employee.getSalary());
//        employeeResponseDTO.setPosition(employee.getPosition());
//        Department department = employee.getDepartment();
//        employeeResponseDTO.setDepartmentId(department != null ? department.getId() : null);
//        employeeResponseDTO.setDepartmentName(department != null ? department.getName() : null);
//        return employeeResponseDTO;
//    }
//}

package employeeManagementSystem.service;

import employeeManagementSystem.dto.DepartmentDTO;
import employeeManagementSystem.dto.DepartmentResponseDTO;
import employeeManagementSystem.dto.EmployeeResponseDTO;
import employeeManagementSystem.dto.ResponseDTO;
import employeeManagementSystem.model.Department;
import employeeManagementSystem.model.Employee;
import employeeManagementSystem.repository.DepartmentRedis;
import employeeManagementSystem.repository.DepartmentRepository;
import employeeManagementSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRedis departmentRedis;

    public ResponseEntity<ResponseDTO> createDepartment(DepartmentDTO departmentDTO) {
        var existingDepartments = departmentRepository.findByName(departmentDTO.getName().toLowerCase());
        if (!existingDepartments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDTO(false, "Department already exists", null));
        }
        var department = new Department();
        department.setName(departmentDTO.getName());
        department.setLocation(departmentDTO.getLocation());

        var savedDepartment = departmentRepository.save(department);
        departmentRedis.saveDepartment(savedDepartment);
        return ResponseEntity.ok(new ResponseDTO(true, "Department created successfully", toDepartmentResponseDTO(savedDepartment)));
    }

    public ResponseEntity<ResponseDTO> getAllDepartments() {
//        var departments = departmentRepository.findAll();
        var departments = departmentRedis.getAllDepartments();
        var departmentResponseDTOS = departments.stream()
                .map(this::toDepartmentResponseDTO)
                .toList();
        return ResponseEntity.ok(new ResponseDTO(true, "Departments fetched successfully", departmentResponseDTOS));
    }

    public ResponseEntity<ResponseDTO> getEmployeesInDepartment(UUID departmentId) {
        var employees = employeeRepository.findByDepartmentId(departmentId);
        var employeeResponseDTOS = employees.stream()
                .map(this::toEmployeeResponseDTO)
                .toList();
        return ResponseEntity.ok(new ResponseDTO(true, "Employees fetched successfully", employeeResponseDTOS));
    }

    private DepartmentResponseDTO toDepartmentResponseDTO(Department department) {
        return new DepartmentResponseDTO(
                department.getId(),
                department.getName(),
                department.getLocation()
        );
    }

    public EmployeeResponseDTO toEmployeeResponseDTO(Employee employee) {
        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(employee.getId());
        employeeResponseDTO.setName(employee.getName());
        employeeResponseDTO.setEmail(employee.getEmail());
        employeeResponseDTO.setSalary(employee.getSalary());
        employeeResponseDTO.setPosition(employee.getPosition());
        Department department = employee.getDepartment();
        employeeResponseDTO.setDepartmentId(department != null ? department.getId() : null);
        employeeResponseDTO.setDepartmentName(department != null ? department.getName() : null);
        return employeeResponseDTO;
    }
}

