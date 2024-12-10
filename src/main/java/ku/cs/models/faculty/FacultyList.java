package ku.cs.models.faculty;

import java.util.ArrayList;

public class FacultyList {
    private ArrayList<Faculty> facultyList = new ArrayList<>();

    public void addFaculty(Faculty faculty) {
        facultyList.add(faculty);
    }

    public void addFaculty(String facultyId,String facultyName) {
        facultyList.add(new Faculty(facultyId,facultyName));
    }

    public void removeFaculty(Faculty faculty) {
        facultyList.remove(faculty);
    }

    public ArrayList<Faculty> getFacultyList() {
        return facultyList;
    }

    public Faculty findByObject(Faculty targetFaculty) {
        for (Faculty faculty : facultyList) {
            if (faculty.equals(targetFaculty)) {
                return faculty;
            }
        }
        return null;
    }

    public Faculty findFacultyById(String id) {
        for (Faculty faculty : facultyList) {
            if (faculty.getFacultyId().equals(id)) {
                return faculty;
            }
        }
        return null;
    }
}
