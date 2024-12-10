package ku.cs.services;

import ku.cs.models.advisor.Advisor;
import ku.cs.models.advisor.AdvisorList;
import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.department.DepartmentStaffList;
import ku.cs.models.faculty.FacultyStaff;
import ku.cs.models.faculty.FacultyStaffList;
import ku.cs.models.student.Student;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class UserListFileDatasource implements Datasource<UserList> {
    private String directoryName;
    private String fileName;
    private DataRepository dataRepository;

    public UserListFileDatasource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();
    }

    private void checkFileIsExisted() {
        File file = new File(directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = directoryName + File.separator + fileName;
        file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public UserList readData() {
        UserList userList = new UserList();
        dataRepository = DataRepository.getDataRepository();

        FacultyStaffList facultyStaffList = dataRepository.getFacultyStaffList();
        AdvisorList advisorList = dataRepository.getAdvisorList();
        DepartmentStaffList departmentStaffList = dataRepository.getDepartmentStaffList();
        StudentList studentList = dataRepository.getStudentList();
        SettingList settingList = dataRepository.getSettingList();

        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        BufferedReader buffer = new BufferedReader(inputStreamReader);

        String line;
        try {

            while ((line = buffer.readLine()) != null) {

                if (line.equals("")) continue;

                String[] data = line.split(",");

                String username = data[0];
                String password = data[1];
                String role = data[2];
                String profilePath = data[3];
                Boolean changedProfile = Boolean.parseBoolean(data[4]);
                Boolean isLoggedIn = Boolean.parseBoolean(data[5]);
                Boolean isAvailable = Boolean.parseBoolean(data[6]);
                Boolean isChangedPassword = Boolean.parseBoolean(data[7]);
                String lastestLogin = data[8];

                User user;
                switch (role) {
                    case "Student":
                        user = studentList.findStudentByUsername(username);
                        break;

                    case "FacultyStaff":
                        user = facultyStaffList.findFacultyStaffByUsername(username);
                        break;

                    case "Advisor":
                        user = advisorList.findAdvisorByUsername(username);
                        break;

                    case "DepartmentStaff":
                        user = departmentStaffList.findDepartmentStaffByUsername(username);
                        break;
                    case "Admin":
                        user = new User(username,password,role);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown role: " + role);
                }
                user.setChangedProfile(changedProfile);
                user.setProfilePath(profilePath);
                user.setAvailable(isAvailable);
                user.setLoggedIn(isLoggedIn);
                user.setUserSetting(settingList.getSetting(user.getUsername()));
                user.setIsChangePassword(isChangedPassword);
                user.setLastestLogin(lastestLogin);

                userList.addUser(user);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }



    @Override
    public void writeData(UserList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);
        dataRepository = DataRepository.getDataRepository();

        FacultyStaffList facultyStaffList = dataRepository.getFacultyStaffList();
        AdvisorList advisorList = dataRepository.getAdvisorList();
        DepartmentStaffList departmentStaffList = dataRepository.getDepartmentStaffList();
        StudentList studentList = dataRepository.getStudentList();

        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);

        try {
            for (User user : data.getUserList()) {
                String line = user.userToString();
                buffer.append(line);
                buffer.append("\n");
                switch (user.getRole()) {
                    case "Student":
                        Student student = studentList.findStudentByUsername(user.getUsername());
                        if(student != null) {
                            student.setPassword(user.getPassword());
                            student.setUsername(user.getUsername());
                            dataRepository.writeData(StudentList.class);
                        }
                        break;

                    case "FacultyStaff":
                        FacultyStaff facultyStaff = facultyStaffList.findFacultyStaffByUsername(user.getUsername());
                        if(facultyStaff != null) {
                            facultyStaff.setPassword(user.getPassword());
                            facultyStaff.setUsername(user.getUsername());
                            dataRepository.writeData(FacultyStaffList.class);
                        }
                        break;

                    case "Advisor":
                        Advisor advisor = advisorList.findAdvisorByUsername(user.getUsername());
                        if(advisor != null) {
                            advisor.setPassword(user.getPassword());
                            advisor.setUsername(user.getUsername());
                            dataRepository.writeData(AdvisorList.class);
                        }
                        break;

                    case "DepartmentStaff":
                        DepartmentStaff departmentStaff = departmentStaffList.findDepartmentStaffByUsername(user.getUsername());
                        if(departmentStaff != null) {
                            departmentStaff.setPassword(user.getPassword());
                            departmentStaff.setUsername(user.getUsername());
                            dataRepository.writeData(DepartmentStaffList.class);
                        }
                        break;
                    case "Admin":
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown role: " + user.getRole());
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
