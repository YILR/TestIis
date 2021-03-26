package testiis.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testiis.model.Department;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class DepartmentDaoTest {

    @Test
    void equal() {
        DepartmentDao departmentDao = new DepartmentDao();
        List<String> oldDep = departmentDao.getAll()
                .stream().map(Department::getDescription).collect(Collectors.toList());
        departmentDao.updateAll(Arrays.asList(new Department("105", "Senior", "Test")));
        departmentDao.insertAll(Arrays.asList(new Department("205", "Junior", "Tester")));
        departmentDao.commitAndClose();
        List<String> newDep = departmentDao.getAll()
                .stream().map(Department::getDescription).collect(Collectors.toList());
        Assertions.assertLinesMatch(oldDep, newDep);
    }

    @Test
    void notEqual() {
        DepartmentDao departmentDao = new DepartmentDao();
        List<String> oldDep = departmentDao.getAll()
                .stream().map(Department::getDescription).collect(Collectors.toList());
        departmentDao.updateAll(Arrays.asList(new Department("105", "Senior", "Test")));
        departmentDao.insertAll(Arrays.asList(new Department("505", "Junior", "Tester")));
        departmentDao.commitAndClose();
        List<String> newDep = departmentDao.getAll()
                .stream().map(Department::getDescription).collect(Collectors.toList());
        Assertions.assertFalse(oldDep.containsAll(newDep));
    }


}