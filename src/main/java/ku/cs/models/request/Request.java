package ku.cs.models.request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

public class Request {
    private String requestId;
    private String requestDate;
    private String requestLastedDated;
    private String requestTopic;
    private String requestType;
    private String requestTo;
    private String requestStatus;
    private String requestNotation;
    private String requestRejectionReason;
    private String specialTopic;

    private String studentAcademic;
    private String studentFaculty;
    private String studentDepartment;
    private String studentPhoneNumber;
    private String studentId;
    private String studentName;
    private String studentAdvisor;
    private String studentEmail;
    private Map<String, String> timeStamps = new TreeMap<>();

    private SubjectList subjectList = new SubjectList();

    public Request() {
        this.requestId = "";
        this.requestDate = "";
        this.requestLastedDated = "";
        this.requestTopic = "";
        this.requestType = "";
        this.requestTo = "";
        this.requestStatus = "";
        this.requestNotation = "";
        this.requestRejectionReason = "-";
        this.specialTopic = "";

        this.studentAcademic = "";
        this.studentFaculty = "";
        this.studentDepartment = "";
        this.studentPhoneNumber = "";
        this.studentId = "";
        this.studentName = "";
        this.studentAdvisor = "";
        this.studentEmail = "";

    }
    public void loadSubjectList(SubjectList subjectList) {
        this.subjectList = subjectList;
    }

    public void addSubject(Subject subject) {
        subjectList.addSubject(subject);
    }

    private void addTimeStamp(String status,String time) { this.timeStamps.put(status, time); }

    //getter
    public String getRequestId() {
        return requestId;
    }

    public String getRequestStatus() {
        return this.requestStatus;
    }

    public String getRequestDate() {
        return requestDate.substring(0, 10);
    }

    public String getRequestLastedDated() { return requestLastedDated; }

    public String getRequestTopic() {
        return requestTopic;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequestTo() { return requestTo ;}

    public String getRequestNotation() { return requestNotation;}

    public String getStudentAcademic() { return studentAcademic; }

    public String getStudentFaculty() { return studentFaculty; }

    public String getStudentDepartment() { return studentDepartment; }

    public String getStudentPhoneNumber() { return studentPhoneNumber; }

    public String getStudentId() { return studentId; }

    public String getStudentName() { return studentName; }

    public String getStudentAdvisor() { return studentAdvisor; }

    public String getStudentEmail() { return studentEmail; }

    public String getSpecialTopic() {
        return specialTopic;
    }

    public SubjectList getSubjectList() {
        return subjectList;
    }

    public String getRequestRejectionReason() {
        return requestRejectionReason;
    }

    public Map<String,String> getTimeStampMap() { return this.timeStamps; }

    //setter

    public void setSpecialReason(String specialTopic) {
        this.specialTopic = specialTopic;
    }

    public void setSpecialTopic(String specialTopic) {
        this.specialTopic = specialTopic;
    }

    public void setRequestStatus(String status) {
        this.requestStatus = status;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public void setRequestLastedDated(String status, String requestLastedDated) {
        this.requestLastedDated = requestLastedDated;
        addTimeStamp(status,requestLastedDated);
    }

    public void setRequestNotation(String notation) {
        this.requestNotation = notation;
    }

    public void setRequestTopic(String requestTopic) {
        this.requestTopic = requestTopic;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setRequestTo(String requestTo) {
        this.requestTo = requestTo;
    }

    public void setStudentAcademic(String year) { this.studentAcademic = year; }

    public void setStudentFaculty(String faculty) { this.studentFaculty = faculty; }

    public void setStudentDepartment(String department) { this.studentDepartment = department; }

    public void setStudentPhoneNumber(String phoneNumber) { this.studentPhoneNumber = phoneNumber; }

    public void setStudentId(String id) { this.studentId = id; }

    public void setStudentName(String name) { this.studentName = name; }

    public void setStudentAdvisor(String advisor) { this.studentAdvisor = advisor; }

    public void setStudentEmail(String email) { this.studentEmail = email; }

    public void setRequestRejectionReason(String rejectionReason) { this.requestRejectionReason = rejectionReason; }

    public void setRequestTimeStamp (Map<String, String> timeStamp) {
        this.timeStamps = timeStamp;
    }

    public boolean isReject(){
        return !requestRejectionReason.equals("-");
    }

    public String textToSearch(){
        return String.join(" ",requestDate,requestTopic,requestType,requestTo,requestStatus).toLowerCase();
    }

    public String staffTextToSearch(){
        return String.join(" ",requestDate,requestTopic,requestType,studentId,studentName).toLowerCase();
    }

    @Override
    public String toString() {
        return String.join(",",
                requestId,
                requestDate,
                requestTopic,
                requestType,
                requestTo,
                requestStatus,
                requestNotation,
                specialTopic,
                studentId,
                studentAcademic,
                studentFaculty,
                studentDepartment,
                studentPhoneNumber,
                studentName,
                studentAdvisor,
                studentEmail,
                requestLastedDated,
                requestRejectionReason);
    }
}
