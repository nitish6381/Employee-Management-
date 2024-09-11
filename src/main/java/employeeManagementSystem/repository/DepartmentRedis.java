package employeeManagementSystem.repository;

import employeeManagementSystem.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentRedis {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String DEPARTMENT = "DEPARTMENT";

    public void saveDepartment(Department department) {
        try {
            redisTemplate.opsForHash().put(DEPARTMENT, department.getId().toString(), department);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<Department> getAllDepartments() {
        return redisTemplate.opsForHash().values(DEPARTMENT);
        }
}
