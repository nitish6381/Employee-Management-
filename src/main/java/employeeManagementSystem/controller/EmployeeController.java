package employeeManagementSystem.controller;

import employeeManagementSystem.dto.EmployeeDTO;
import employeeManagementSystem.dto.ResponseDTO;
import employeeManagementSystem.model.Employee;
import employeeManagementSystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllEmployees(
    ) {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getEmployeeById(@PathVariable UUID id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateEmployee(@PathVariable UUID id, @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.updateEmployee(employeeDTO, id);
    }
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
    }
}
