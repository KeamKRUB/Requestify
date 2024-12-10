package ku.cs.models.department;

import java.util.ArrayList;

public class DepartmentList {
    private final ArrayList<Department> departmentList = new ArrayList<>();

    public void addDepartment(Department department){
        departmentList.add(department);
    }

    public void removeDepartment(Department department){
        departmentList.remove(department);
    }

    public ArrayList<Department> getDepartmentList(){
        return departmentList;
    }

    public ArrayList<Department> getDepartmentListByFaculty(String facultyName){
        ArrayList<Department> departmentListByFaculty = new ArrayList<>();
        for(Department department : departmentList){
            if(department.getFacultyName().equals(facultyName)){
                departmentListByFaculty.add(department);
            }
        }
        return departmentListByFaculty;
    }

    public Department findDepartmentById(String id) {
        for (Department department : departmentList) {
            if (department.getDepartmentId().equals(id)) {
                return department;
            }
        }
        return null;
    }
}
