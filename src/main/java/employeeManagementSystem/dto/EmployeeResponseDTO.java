package employeeManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {private UUID id;
    private String name;
    private String email;
    private String position;
    private double salary;
    private UUID departmentId;
    private String departmentName;
}
