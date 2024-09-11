package employeeManagementSystem.controller;

import employeeManagementSystem.dto.DepartmentDTO;
import employeeManagementSystem.dto.ResponseDTO;
import employeeManagementSystem.model.Department;
import employeeManagementSystem.model.Employee;
import employeeManagementSystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        return departmentService.createDepartment(departmentDTO);
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<ResponseDTO> getEmployeesInDepartment(@PathVariable UUID id) {
        return departmentService.getEmployeesInDepartment(id);
    }
}
