package employeeManagementSystem.repository;

import employeeManagementSystem.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    @Query("SELECT d FROM Department d WHERE LOWER(d.name) = :name")
    List<Department> findByName(String name);
}
