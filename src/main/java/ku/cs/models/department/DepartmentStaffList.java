package ku.cs.models.department;

import ku.cs.models.faculty.Faculty;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.util.ArrayList;

public class DepartmentStaffList {
    private ArrayList<DepartmentStaff> departmentStaffList = new ArrayList<>();

    public void addStaff(DepartmentStaff staff) {
        departmentStaffList.add(staff);
    }

    public void addStaff(String username,String password, String role,String firstName, String lastName, String faculty, String department) {
        departmentStaffList.add(new DepartmentStaff(username,password,role,firstName,lastName,faculty,department));
    }

    public void removeStaff(DepartmentStaff staff) {
        departmentStaffList.remove(staff);
    }

    public void removeAllStaff(ArrayList<DepartmentStaff> staffs) {
        DataRepository dataRepository = DataRepository.getDataRepository();
        SettingList settingList = dataRepository.getSettingList();
        UserList userList = dataRepository.getUserList();
        for (DepartmentStaff staff : staffs) {
            settingList.removeSetting(staff.getUserSetting());
        }
        departmentStaffList.removeAll(staffs);
        userList.removeAllUser(userList);
    }

    public DepartmentStaff findByObject(DepartmentStaff targetStaff) {
        for (DepartmentStaff staff : departmentStaffList) {
            if (staff.equals(targetStaff)) {
                return staff;
            }
        }
        return null;
    }
    public DepartmentStaff findDepartmentStaffByUsername(String username) {
        for (DepartmentStaff staff : departmentStaffList) {
            if (staff.getUsername().equals(username)) {
                return staff;
            }
        }
        return null;
    }


    public ArrayList<DepartmentStaff> getDepartmentStaffList() {
        return departmentStaffList;
    }

    public DepartmentStaffList getDepartmentStaffListByDepartment(String department) {
        DepartmentStaffList thisDepartmentStaffList = new DepartmentStaffList();
        for (DepartmentStaff staff : departmentStaffList) {
            if (staff.getDepartment().equals(department)) {
                thisDepartmentStaffList.addStaff(staff);
            }
        }
        return thisDepartmentStaffList;
    }

    public DepartmentStaffList getDepartmentStaffListByFaculty(String faculty) {
        DepartmentStaffList thisDepartmentStaffList = new DepartmentStaffList();
        for (DepartmentStaff staff : departmentStaffList) {
            if (staff.getFaculty().equals(faculty)) {
                thisDepartmentStaffList.addStaff(staff);
            }
        }
        return thisDepartmentStaffList;
    }
}
