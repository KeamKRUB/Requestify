package ku.cs.models.student;


import ku.cs.models.user.UserList;
import ku.cs.services.DataRepository;

import java.util.ArrayList;

public class StudentList {
    public Student[] getStudentList;
    private ArrayList<Student> studentList = new ArrayList<>();

    public void addStudent(Student student) {
        studentList.add(student);
    }

    public void addStudent(String username, String password, String role, String studentId, String firstName, String lastName, String email, String advisor, String department,String faculty) {
        studentList.add(new Student(username, password, role, studentId, firstName, lastName, email, advisor, department,faculty));
    }

    public void removeStudent(Student student) {
        studentList.remove(student);
    }

    public ArrayList<Student> getStudentList() {
        return studentList;
    }

    public Student findByStudent(Student targetStudent) {
        for (Student student : studentList) {
            if (student.equals(targetStudent)) {
                return student;
            }
        }
        return null;
    }
    public Student findStudentByUsername(String username) {
        for (Student student : studentList) {
            if (student.getUsername().equals(username)) {
                return student;
            }
        }
        return null;
    }

    public Student findStudentById(String id) {
        for (Student student : studentList) {
            if (student.getStudentId().equals(id)) {
                return student;
            }
        }
        return null;
    }

    public StudentList getStudentsListByAdvisor (String advisor) {
        StudentList studentListByAdvisor = new StudentList();
        for (Student student : studentList) {
            if (student.getAdvisor().equals(advisor)) {
                studentListByAdvisor.addStudent(student);
            }
        }
        return studentListByAdvisor;
    }

    public StudentList getStudentListByDepartment (String department) {
        StudentList studentListByDepartment = new StudentList();
        for (Student student : studentList) {
            if (student.getDepartment().equals(department)) {
                studentListByDepartment.addStudent(student);
            }
        }
        return studentListByDepartment;
    }

    public StudentList getStudentListByFaculty (String faculty) {
        StudentList studentListByFaculty = new StudentList();
        for (Student student : studentList) {
            if (student.getFaculty().equals(faculty)) {
                studentListByFaculty.addStudent(student);
            }
        }
        return studentListByFaculty;
    }

    public StudentList getRegisteredStudents() {
        UserList userList = DataRepository.getDataRepository().getUserList();
        StudentList registeredStudents = new StudentList();
        for (Student student : studentList) {
            if(userList.findUserByUsername(student.getUsername()) != null) {
                registeredStudents.addStudent(student);
            }
        }
        return registeredStudents;
    }
}
