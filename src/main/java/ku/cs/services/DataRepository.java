package ku.cs.services;

import ku.cs.models.advisor.Advisor;
import ku.cs.models.advisor.AdvisorList;
import ku.cs.models.department.*;
import ku.cs.models.faculty.*;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.request.SubjectList;
import ku.cs.models.student.Student;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;
import ku.cs.models.user.User;
import ku.cs.models.user.UserList;

import java.util.HashMap;
import java.util.Map;

public class DataRepository {

    private static DataRepository dataRepository;

    private StudentList studentList;
    private AdvisorList advisorList;
    private FacultyStaffList facultyStaffList;
    private RequestList requestList;
    private UserList userList;
    private SubjectList subjectList;

    private DepartmentStaffList departmentStaffList;
    private DepartmentList departmentList;

    private FacultyCertifierList facultyCertifierList;
    private FacultyList facultyList;

    private DepartmentCertifierList departmentCertifierList;

    private SettingList settingList;

    private final Map<Class<?>, Datasource<?>> datasourceMap = new HashMap<>();
    private final Map<Class<?>, Object> dataMap = new HashMap<>();


    private DataRepository() {
        initializeDataSources();

        studentList = loadData(StudentList.class);
        advisorList = loadData(AdvisorList.class);
        facultyStaffList = loadData(FacultyStaffList.class);
        departmentStaffList = loadData(DepartmentStaffList.class);
        departmentCertifierList = loadData(DepartmentCertifierList.class);
        requestList = loadData(RequestList.class);
        facultyCertifierList = loadData(FacultyCertifierList.class);
        facultyList = loadData(FacultyList.class);
        departmentList = loadData(DepartmentList.class);
        settingList = loadData(SettingList.class);
        subjectList = loadData(SubjectList.class);

        initializeData();
    }

    private void initializeDataSources() {
        datasourceMap.put(StudentList.class, new StudentListFileDatasource("data", "student-list.csv"));
        datasourceMap.put(AdvisorList.class, new AdvisorListFileDatasource("data", "advisor-list.csv"));
        datasourceMap.put(FacultyStaffList.class, new FacultyStaffListFileDatasource("data", "faculty-staff-list.csv"));
        datasourceMap.put(DepartmentStaffList.class, new DepartmentStaffListFileDatasource("data", "department-staff-list.csv"));
        datasourceMap.put(UserList.class, new UserListFileDatasource("data", "user-list.csv"));
        datasourceMap.put(SubjectList.class, new SubjectListFileDatasource("data", "subject-list.csv"));
        datasourceMap.put(RequestList.class, new RequestListFileDatasource("data", "request-list.csv"));

        datasourceMap.put(FacultyList.class, new FacultyListFileDatasource("data","faculty-list.csv"));
        datasourceMap.put(FacultyCertifierList.class, new FacultyCertifierListFileDatasource("data", "faculty-certifier-list.csv"));

        datasourceMap.put(DepartmentList.class, new DepartmentListFileDatasource("data","department-list.csv"));
        datasourceMap.put(DepartmentCertifierList.class, new DepartmentCertifierListFileDatasource("data", "department-certifier-list.csv"));

        datasourceMap.put(SettingList.class, new SettingListFileDatasource("data", "setting-list.csv"));
    }

    private void initializeData() {
        dataMap.put(StudentList.class, studentList);
        dataMap.put(AdvisorList.class, advisorList);
        dataMap.put(FacultyStaffList.class, facultyStaffList);
        dataMap.put(FacultyList.class, facultyList);
        dataMap.put(FacultyCertifierList.class, facultyCertifierList);
        dataMap.put(DepartmentStaffList.class, departmentStaffList);
        dataMap.put(DepartmentCertifierList.class, departmentCertifierList);
        dataMap.put(UserList.class, userList);
        dataMap.put(RequestList.class,requestList);
        dataMap.put(DepartmentList.class, departmentList);
        dataMap.put(SettingList.class, settingList);
        dataMap.put(SubjectList.class, subjectList);

    }

    public static DataRepository getDataRepository() {
        if (dataRepository == null) {
            dataRepository = new DataRepository();
        }
        return dataRepository;
    }

    private <T> T loadData(Class<T> type) {
        Datasource<T> datasource = (Datasource<T>) datasourceMap.get(type);
        if (datasource == null) {
            throw new IllegalArgumentException("Datasource not found " + type);
        }
        return datasource.readData();
    }

    public <T> void reloadData(Class<T> type) {
        if(dataMap.get(type) == null) {
            return;
        } //ห้ามไม่ให้เข้าถึงข้อมูลที่ไม่ได้เป็นของ role นั้นๆ
        T data = loadData(type);
        dataMap.put(type, data);
    }

//    public void loadDataForUser(User user) {
//        switch (user) {
//            case Student student -> {
//                requestList = loadData(RequestList.class);
//                dataMap.put(RequestList.class, requestList);
//                for (Request request : requestList.getRequests()) {
//                    if (request.getStudentId().equals(student.getId())) {
//                        student.addRequest(request);
//                    }
//                }
//            }
//            case Advisor advisor -> {
//                requestList = loadData(RequestList.class);
//                dataMap.put(RequestList.class, requestList);
//                advisor.loadRequestList(requestList.getRequestByAdvisor(advisor.getName()));
//
//                facultyCertifierList = loadData(FacultyCertifierList.class);
//                dataMap.put(FacultyCertifierList.class, facultyCertifierList);
//
//            }
//            case FacultyStaff facultyStaff -> {
//                requestList = loadData(RequestList.class);
//                dataMap.put(RequestList.class, requestList);
//                facultyStaff.loadRequestList(requestList.getRequestByFaculty(facultyStaff.getFaculty()));
//
//                facultyCertifierList = loadData(FacultyCertifierList.class);
//                dataMap.put(FacultyCertifierList.class, facultyCertifierList);
//
//            }
//            default -> throw new IllegalArgumentException("Unsupported user type: " + user.getClass().getName());
//        }
//    }

    public <T> void writeData(Class<T> type) {
        Datasource<T> datasource = (Datasource<T>) datasourceMap.get(type);
        T data = (T) dataMap.get(type);
        if (datasource != null && data != null) {
            datasource.writeData(data);
        }
        else{
            System.out.println(data);
        }
    }

    public static void clearInstance() {
        dataRepository = null;
    }

    public StudentList getStudentList() {
        return (StudentList) dataMap.get(StudentList.class);
    }
    public AdvisorList getAdvisorList() {
        return (AdvisorList) dataMap.get(AdvisorList.class);
    }
    public SubjectList getSubjectList() { return (SubjectList) dataMap.get(SubjectList.class); }

    public FacultyStaffList getFacultyStaffList(){
        return (FacultyStaffList) dataMap.get(FacultyStaffList.class);
    }

    public DepartmentStaffList getDepartmentStaffList() {
        return (DepartmentStaffList) dataMap.get(DepartmentStaffList.class);
    }

    public UserList getUserList() {
        userList = loadData(UserList.class);
        dataMap.put(UserList.class, userList);
        return (UserList) dataMap.get(UserList.class);
    }

    public RequestList getRequestList() {
        return (RequestList) dataMap.get(RequestList.class);
    }
    public FacultyCertifierList getFacultyCertifierList() {
        return (FacultyCertifierList) dataMap.get(FacultyCertifierList.class);
    }
    public DepartmentCertifierList getDepartmentCertifierList(){
        return (DepartmentCertifierList) dataMap.get(DepartmentCertifierList.class);
    }

    public FacultyList getFacultyList(){
        return (FacultyList) dataMap.get(FacultyList.class);
    }

    public DepartmentList getDepartmentList(){
        return (DepartmentList) dataMap.get(DepartmentList.class);
    }

    public SettingList getSettingList() {
        return (SettingList) dataMap.get(SettingList.class);
    }
}
