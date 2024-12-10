package ku.cs.models.request;

public class Subject {
    private String subjectId;
    private String subjectName;
    private String enrollType;
    private String lectureSection;
    private String labSection;
    private String lectureCredit;
    private String labCredit;
    private String requestId;

    public Subject(String subjectId, String subjectName, String enrollType,
                   String lectureSection, String labSection, String lectureCredit, String labCredit, String requestId) {

        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.enrollType = enrollType;
        this.lectureSection = lectureSection;
        this.labSection = labSection;
        this.lectureCredit = lectureCredit;
        this.labCredit = labCredit;
        this.requestId = requestId;
    }

    public Subject() {
        this.subjectId = "";
        this.subjectName = "";
        this.enrollType = "";
        this.lectureSection = "";
        this.labSection = "";
        this.lectureCredit = "";
        this.labCredit = "";
    }

    public Subject(String subjectId, String subjectName, String requestId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.requestId = requestId;
    }

    public String getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName; }
    public String getEnrollType() { return enrollType; }
    public String getLectureSection() { return lectureSection; }
    public String getLabSection() { return labSection; }
    public String getLectureCredit() { return lectureCredit; }
    public String getLabCredit() { return labCredit; }
    public String getRequestId() { return requestId; }

    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public void setEnrollType(String enrollType) { this.enrollType = enrollType; }
    public void setLectureSection(String lectureSection) { this.lectureSection = lectureSection; }
    public void setLabSection(String labSection) { this.labSection = labSection; }
    public void setLectureCredit(String lectureCredit) { this.lectureCredit = lectureCredit; }
    public void setLabCredit(String labCredit) { this.labCredit = labCredit; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String toString() {
        return String.join(",",subjectId,subjectName,enrollType,lectureSection,labSection,lectureCredit,labCredit,requestId);
    }
}