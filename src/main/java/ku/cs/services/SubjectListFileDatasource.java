package ku.cs.services;


import ku.cs.models.request.Subject;
import ku.cs.models.request.SubjectList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SubjectListFileDatasource implements Datasource<SubjectList> {
    private String directoryName;
    private String fileName;

    public SubjectListFileDatasource(String directoryName, String fileName) {
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
    public SubjectList readData() {
        SubjectList subjectList = new SubjectList();
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

                // สร้าง Subject object โดยใช้ default constructor
                Subject subject = new Subject();

                // ตั้งค่าผ่าน setter methods
                subject.setSubjectId(data[0]);
                subject.setSubjectName(data[1]);
                subject.setEnrollType(data[2]);
                subject.setLectureSection(data[3]);
                subject.setLabSection(data[4]);
                subject.setLectureCredit(data[5]);
                subject.setLabCredit(data[6]);
                subject.setRequestId(data[7]);

                // เพิ่ม subject ลงใน RequestList
                subjectList.addSubject(subject);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return subjectList;
    }

    @Override
    public void writeData(SubjectList data) {
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
            for (Subject subject : data.getSubjects()) {
                String line = subject.toString();

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
