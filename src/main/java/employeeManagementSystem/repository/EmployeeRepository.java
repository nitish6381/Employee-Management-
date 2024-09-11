package employeeManagementSystem.repository;

import employeeManagementSystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    @Query("SELECT e FROM Employee e WHERE LOWER(e.email) = :email")
    List<Employee> findByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId")
    List<Employee> findByDepartmentId(UUID departmentId);
}
