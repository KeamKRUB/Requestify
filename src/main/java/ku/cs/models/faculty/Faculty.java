package ku.cs.models.faculty;

public class Faculty{

    private String facultyId;
    private String facultyName;

    public Faculty(String facultyId, String facultyName) {
        this.facultyId = facultyId;
        this.facultyName = facultyName;
    }
    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
    public String getFacultyId() {
        return facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Faculty)) return false;
        Faculty faculty = (Faculty) obj;
        return getFacultyId().equals(faculty.getFacultyId());
    }

    @Override
    public String toString(){

        return String.join(",", facultyId, facultyName);
    }

    public String textToSearch(){
        return String.join(" ", facultyId, facultyName).toLowerCase();
    }
}
