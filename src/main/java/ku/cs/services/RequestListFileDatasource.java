package ku.cs.services;

import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class RequestListFileDatasource implements Datasource<RequestList> {
    private String directoryName;
    private String fileName;

    public RequestListFileDatasource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();
    }

    // ตรวจสอบว่ามีไฟล์ให้อ่านหรือไม่ ถ้าไม่มีให้สร้างไฟล์เปล่า
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
    public RequestList readData() {
        RequestList requestList = new RequestList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        // เตรียม object ที่ใช้ในการอ่านไฟล์
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream,
                StandardCharsets.UTF_8
        );
        BufferedReader buffer = new BufferedReader(inputStreamReader);

        String line;
        try {
            // ใช้ while loop เพื่ออ่านข้อมูลรอบละบรรทัด
            while ((line = buffer.readLine()) != null) {
                // ถ้าเป็นบรรทัดว่าง ให้ข้าม
                if (line.equals("")) continue;

                // แยกสตริงด้วย ,
                String[] data = line.split(",");

                // สร้าง Request object โดยใช้ default constructor
                Request request = new Request();

                // ตั้งค่าผ่าน setter methods
                request.setRequestId(data[0]);
                request.setRequestDate(data[1]);
                request.setRequestTopic(data[2]);
                request.setRequestType(data[3]);
                request.setRequestTo(data[4]);
                request.setRequestStatus(data[5]);
                request.setRequestNotation(data[6]);
                request.setSpecialTopic(data[7]);
                request.setStudentId(data[8]);
                request.setStudentAcademic(data[9]);
                request.setStudentFaculty(data[10]);
                request.setStudentDepartment(data[11]);
                request.setStudentPhoneNumber(data[12]);
                request.setStudentName(data[13]);
                request.setStudentAdvisor(data[14]);
                request.setStudentEmail(data[15]);
                request.setRequestLastedDated(data[5],data[16]);
                request.setRequestRejectionReason(data[17]);
                String timeStampData = data[18];
                Map<String, String> timeStamps = new TreeMap<>();

                String[] timeStampEntries = timeStampData.split(";");
                for (String time : timeStampEntries) {
                    String[] keyValue = time.split("_");
                    if(keyValue.length == 2) {
                        timeStamps.put(keyValue[0], keyValue[1]);
                    }
                }
                request.setRequestTimeStamp(timeStamps);
                // เพิ่ม request ลงใน RequestList
                requestList.addRequest(request);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return requestList;
    }

    @Override
    public void writeData(RequestList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        // เตรียม object ที่ใช้ในการเขียนไฟล์
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream,
                StandardCharsets.UTF_8
        );
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);

        try {
            // สร้าง csv ของ Request และเขียนลงในไฟล์ทีละบรรทัด
            for (Request request : data.getRequests()) {
                String line = request.toString();

                Map<String, String> timeStamps = request.getTimeStampMap();
                StringBuilder timeStampData = new StringBuilder();
                for (Map.Entry<String, String> entry : timeStamps.entrySet()) {
                    if (timeStampData.length() > 0) {
                        timeStampData.append(";");
                    }
                    timeStampData.append(entry.getKey()).append("_").append(entry.getValue());
                }

                line = line + "," + timeStampData;
                buffer.append(line);
                buffer.append("\n");
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
