//package employeeManagementSystem.service;
//
//import employeeManagementSystem.dto.EmployeeDTO;
//import employeeManagementSystem.dto.EmployeeResponseDTO;
//import employeeManagementSystem.dto.ResponseDTO;
//import employeeManagementSystem.model.Department;
//import employeeManagementSystem.model.Employee;
//import employeeManagementSystem.repository.DepartmentRepository;
//import employeeManagementSystem.repository.EmployeeRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Repository;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class EmployeeService {
//    @Autowired
//    private EmployeeRepository employeeRepository;
//    @Autowired
//    private DepartmentRepository departmentRepository;
//
//    public ResponseEntity<ResponseDTO> getAllEmployees() {
//        List<Employee> employees = employeeRepository.findAll();
//        List<EmployeeResponseDTO> employeeResponseDTOList = employees.stream().map(this::getEmployeeResponseDTO).toList();
//        return ResponseEntity.ok(new ResponseDTO(true, "Employees fetched successfully", employeeResponseDTOList));
//    }
//
//    public ResponseEntity<ResponseDTO> getEmployeeById(UUID id) {
//        Optional<Employee> employee = employeeRepository.findById(id);
//        if(employee.isPresent()){
//            return ResponseEntity.ok(new ResponseDTO(true, "", getEmployeeResponseDTO(employee.get())));
//        } else {
//            return ResponseEntity.ok(new ResponseDTO(false, "Employee not found", new ArrayList<>()));
//        }
//    }
//
//    public ResponseEntity<ResponseDTO> createEmployee(EmployeeDTO employeeDTO) {
//        List<Employee> employees = employeeRepository.findByEmail(employeeDTO.getEmail().toLowerCase());
//        if (!employees.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDTO(false, "Employee already exists", null));
//        }
//        Employee employee = new Employee();
//        employee.setName(employeeDTO.getName());
//        employee.setSalary(employeeDTO.getSalary());
//        employee.setEmail(employeeDTO.getEmail());
//        if(employeeDTO.getDepartmentId() == null) {
//            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Department ID is required", null));
//        }
//        Optional<Department> department = departmentRepository.findById(employeeDTO.getDepartmentId());
//        employee.setPosition(employeeDTO.getPosition());
//        employee.setDepartment(department.orElse(null));
//        if(employeeRepository.save(employee) != null) {
//            return ResponseEntity.ok(new ResponseDTO(true, "Employee created successfully", getEmployeeResponseDTO(employee)));
//        } else {
//            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Employee creation failed", null));
//        }
//    }
//
//    public ResponseEntity<ResponseDTO> UpdateEmployee(EmployeeDTO employeeDTO, UUID id) {
//        Optional<Employee> employee = employeeRepository.findById(id);
//        if (employee.isPresent()) {
//            employee.get().setName(employeeDTO.getName());
//            employee.get().setSalary(employeeDTO.getSalary());
//            employee.get().setEmail(employeeDTO.getEmail());
//            Optional<Department> department = departmentRepository.findById(employeeDTO.getDepartmentId());
//            employee.get().setPosition(employeeDTO.getPosition());
//            employee.get().setDepartment(department.orElse(null));
//            return ResponseEntity.ok(new ResponseDTO(true, "", employeeRepository.save(employee.get())));
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(false, "Employee not found", null));
//        }
//    }
//
//    public ResponseEntity<ResponseDTO> deleteEmployee(UUID id) {
//        Optional<Employee> employee = employeeRepository.findById(id);
//        if(employee.isPresent()){
//            employeeRepository.deleteById(id);
//            return ResponseEntity.ok(new ResponseDTO(true, "Sucessfully Deleted", new ArrayList<>()));
//        } else {
//            return ResponseEntity.ok(new ResponseDTO(false, "Failed Deleted", new ArrayList<>()));
//        }
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

import employeeManagementSystem.dto.EmployeeDTO;
import employeeManagementSystem.dto.EmployeeResponseDTO;
import employeeManagementSystem.dto.ResponseDTO;
import employeeManagementSystem.model.Department;
import employeeManagementSystem.model.Employee;
import employeeManagementSystem.repository.DepartmentRepository;
import employeeManagementSystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    public ResponseEntity<ResponseDTO> getAllEmployees() {
        var employees = employeeRepository.findAll();
        var employeeResponseDTOList = employees.stream()
                .map(this::toEmployeeResponseDTO)
                .toList();
        return ResponseEntity.ok(new ResponseDTO(true, "Employees fetched successfully", employeeResponseDTOList));
    }

    public ResponseEntity<ResponseDTO> getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .map(employee -> ResponseEntity.ok(new ResponseDTO(true, "", toEmployeeResponseDTO(employee))))
                .orElseGet(() -> ResponseEntity.ok(new ResponseDTO(false, "Employee not found", new ArrayList<>())));
    }

    public ResponseEntity<ResponseDTO> createEmployee(EmployeeDTO employeeDTO) {
        var email = employeeDTO.getEmail().toLowerCase();
        if (!employeeRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDTO(false, "Employee already exists", null));
        }
        if (employeeDTO.getDepartmentId() == null) {
            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Department ID is required", null));
        }
        var department = departmentRepository.findById(employeeDTO.getDepartmentId())
                .orElse(null);
        var employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setSalary(employeeDTO.getSalary());
        employee.setEmail(email);
        employee.setPosition(employeeDTO.getPosition());
        employee.setDepartment(department);
        var savedEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(new ResponseDTO(true, "Employee created successfully", toEmployeeResponseDTO(savedEmployee)));
    }

    public ResponseEntity<ResponseDTO> updateEmployee(EmployeeDTO employeeDTO, UUID id) {
        if (employeeDTO.getDepartmentId() == null) {
            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Department ID is required", null));
        }
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(employeeDTO.getName());
                    employee.setSalary(employeeDTO.getSalary());
                    employee.setEmail(employeeDTO.getEmail());
                    employee.setPosition(employeeDTO.getPosition());
                    var department = departmentRepository.findById(employeeDTO.getDepartmentId())
                            .orElse(null);
                    employee.setDepartment(department);
                    var updatedEmployee = employeeRepository.save(employee);
                    return ResponseEntity.ok(new ResponseDTO(true, "", toEmployeeResponseDTO(updatedEmployee)));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(false, "Employee not found", null)));
    }

    public ResponseEntity<ResponseDTO> deleteEmployee(UUID id) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employeeRepository.deleteById(id);
                    return ResponseEntity.ok(new ResponseDTO(true, "Successfully Deleted", new ArrayList<>()));
                })
                .orElseGet(() -> ResponseEntity.ok(new ResponseDTO(false, "Failed to Delete", new ArrayList<>())));
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
