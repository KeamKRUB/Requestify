package ku.cs.services;

import ku.cs.models.faculty.FacultyCertifier;
import ku.cs.models.faculty.FacultyCertifierList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FacultyCertifierListFileDatasource implements Datasource<FacultyCertifierList>{
    private String directoryName;
    private String fileName;

    public FacultyCertifierListFileDatasource(String directoryName, String fileName) {
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
    public FacultyCertifierList readData() {
        FacultyCertifierList facultyCertifiers = new FacultyCertifierList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        // เตรียม object ที่ใช้ในการอ่านไฟล์
        FileInputStream fileInputStream = null;
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

        String line = "";
        try {
            // ใช้ while loop เพื่ออ่านข้อมูลรอบละบรรทัด
            while ( (line = buffer.readLine()) != null ){
                // ถ้าเป็นบรรทัดว่าง ให้ข้าม
                if (line.equals("")) continue;

                // แยกสตริงด้วย ,
                String[] data = line.split(",");

                // อ่านข้อมูลตาม index แล้วจัดการประเภทของข้อมูลให้เหมาะสม
                String firstName = data[0].trim();
                String lastName = data[1].trim();
                String faculty = data[2].trim();
                String position = data[3].trim();

                // เพิ่มข้อมูลลงใน list
                facultyCertifiers.addCertifier(firstName,lastName,faculty,position);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return facultyCertifiers;
    }

    @Override
    public void writeData(FacultyCertifierList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        // เตรียม object ที่ใช้ในการเขียนไฟล์
        FileOutputStream fileOutputStream = null;

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
            // สร้าง csv ของ FacultyCertifier และเขียนลงในไฟล์ทีละบรรทัด
            for (FacultyCertifier certifier : data.getCertifierList()) {
                String line = certifier.toString();
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}