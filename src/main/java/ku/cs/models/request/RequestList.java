package ku.cs.models.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestList {

    private final ArrayList<Request> requests = new ArrayList<>();

    public void addRequest(Request request) {
        requests.add(request);
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public List<Request> getRequestsByStatus(String status) {
        return requests.stream()
                .filter(request -> status.equals(request.getRequestStatus()))
                .collect(Collectors.toList());
    }
    public RequestList getRequestsByRequestTo(String requestTo) {
        RequestList requestsTo = new RequestList();
        for (Request request : requests) {
            if (request.getRequestTo().contains(requestTo)) {
                requestsTo.addRequest(request);
            }
        }
        return requestsTo;
    }

    public void removeRequest(Request request) {
        requests.remove(request);
    }

    public Request getRequest(int index) {
        if (index >= 0 && index < requests.size()) {
            return requests.get(index);
        } else {
            return null;
        }
    }

    public int getTotalRequests() {
        return requests.size();
    }

    public int getTotalRequestsByStatus(String status) {
        ArrayList<Request> statusRequests = new ArrayList<>();
        for (Request request : requests) {
            if (request.getRequestStatus().contains(status)) {
                statusRequests.add(request);
            }
        }
        return statusRequests.size();
    }

    public int getTotalRequestsByRequestTo(String requestTo) {
        ArrayList<Request> statusRequests = new ArrayList<>();
        for (Request request : requests) {
            if (request.getRequestTo().contains(requestTo)) {
                statusRequests.add(request);
            }
        }
        return statusRequests.size();
    }

    public void clearRequests() {
        requests.clear();
    }

    public ArrayList<Request> UserFindRequest(String keyword) {
        ArrayList<Request> foundRequests = new ArrayList<>();

        for (Request request : requests) {
            if (request.getRequestTopic().contains(keyword)) {
                foundRequests.add(request);
            }
        }

        return foundRequests;
    }

    public RequestList getRequestByStudentId(String studentId) {
        RequestList studentRequest = new RequestList();
        for (Request request : requests) {
            if (request.getStudentId().equals(studentId)) {
                studentRequest.addRequest(request);
            }
        }
        return studentRequest;
    }

    public RequestList getRequestByAdvisor(String advisor) {
        RequestList advisorRequest = new RequestList();
        for (Request request : requests) {
            if (request.getStudentAdvisor().equals(advisor)) {
                advisorRequest.addRequest(request);
            }
        }
        return advisorRequest;
    }
    public RequestList getRequestByDepartment(String department) {
        RequestList departmentRequest = new RequestList();
        for (Request request : requests) {
            if (request.getStudentDepartment().equals(department)) {
                departmentRequest.addRequest(request);
            }
        }
        return departmentRequest;
    }

    public RequestList getRequestByFaculty(String faculty) {
        RequestList facultyRequests = new RequestList();
        for (Request request : requests) {
            if (request.getStudentFaculty().equals(faculty)) {
                facultyRequests.addRequest(request);
            }
        }
        return facultyRequests;
    }

    public Request findByObject(Request targetRequest) {
        for (Request request : requests) {
            if (request.equals(targetRequest)) {
                return request;
            }
        }
        return null;
    }

    public int getSize(){
        return requests.size();
    }

}