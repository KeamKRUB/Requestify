package ku.cs.models.faculty;

import ku.cs.models.user.SettingList;
import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.util.ArrayList;

public class FacultyStaffList {
    private ArrayList<FacultyStaff> facultyStaffList = new ArrayList<>();

    public void addStaff(FacultyStaff staff) {
        facultyStaffList.add(staff);
    }

    public void addStaff(String username,String password, String role,String firstName, String lastName, String faculty) {
        facultyStaffList.add(new FacultyStaff(username,password,role,firstName,lastName,faculty));
    }

    public void removeStaff(FacultyStaff staff) {
        facultyStaffList.remove(staff);
    }

    public void removeAllStaff(ArrayList<FacultyStaff> staffs) {
        DataRepository dataRepository = DataRepository.getDataRepository();
        SettingList settingList = dataRepository.getSettingList();
        UserList userList = dataRepository.getUserList();
        for (FacultyStaff staff : staffs) {
            settingList.removeSetting(staff.getUserSetting());
        }
        facultyStaffList.removeAll(staffs);
        userList.removeAllUser(userList);
    }

    public FacultyStaff findByObject(FacultyStaff targetStaff) {
        for (FacultyStaff staff : facultyStaffList) {
            if (staff.equals(targetStaff)) {
                return staff;
            }
        }
        return null;
    }

    public FacultyStaff findFacultyStaffByUsername(String username) {
        for (FacultyStaff staff : facultyStaffList) {
            if (staff.getUsername().equals(username)) {
                return staff;
            }
        }
        return null;
    }


    public ArrayList<FacultyStaff> getFacultyStaffList() {
        return facultyStaffList;
    }

    public FacultyStaffList getFacultyStaffListByFaculty(String faculty) {
        FacultyStaffList thisFacultyStaffList = new FacultyStaffList();
        for (FacultyStaff staff : facultyStaffList) {
            if (staff.getFaculty().equals(faculty)) {
                thisFacultyStaffList.addStaff(staff);
            }
        }
        return thisFacultyStaffList;
    }
}
