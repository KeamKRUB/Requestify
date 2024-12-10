package ku.cs.services;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import ku.cs.controllers.student.StudentRequestConfirmationController;
import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;
import ku.cs.models.request.Subject;
import ku.cs.models.request.SubjectList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class PdfUtil {
    private Request request;

    public PdfUtil(Request request) {
        this.request = request;
    }

    static float threecol = 190f;
    static float twocol = 285f;
    static float twocol150 = twocol + 150f;
    static float[] twocolmnWidth = {twocol150, twocol};
    static float[] fullWidth = {threecol * 3};

    public void createRequestPDF() throws IOException {
        String fileName = request.getStudentDepartment() + "_" + request.getStudentDepartment() + "_" + "requestNo" + request.getRequestId() + ".pdf";
        String directoryPath = "data/requests/";

        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        PdfWriter pdfWriter = new PdfWriter(directoryPath + fileName);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDocument);

        InputStream in = StudentRequestConfirmationController.class.getResourceAsStream("/fonts/THSarabunPSK.ttf");
        byte[] targetArray = null;
        try {
            targetArray = new byte[in.available()];
            in.read(targetArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FontProgram fontProgram = FontProgramFactory.createFont(targetArray);
        PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, false);
        document.setFont(font).setFontSize(16);

        Paragraph onesp = new Paragraph("\n");

        Border greyBorder = new SolidBorder(Color.GRAY, 1f / 2f);
        Table divider = new Table(fullWidth);
        divider.setBorder(greyBorder);

        document.add(getHeader(request,1)).setFont(font);
        document.add(divider);

        Table nestedTable2 = new Table(new float[]{twocol / 5, twocol / 4});
        nestedTable2.addCell(getHeaderTextCell("Send to"));
        nestedTable2.addCell(getHeaderTextCellValue(request.getStudentAdvisor()));
        document.add(nestedTable2.setFont(font));

        Table twoColTable2 = new Table(twocolmnWidth).setBorder(Border.NO_BORDER); // ตั้งค่าตารางหลักให้ไม่มีขอบ
        twoColTable2.addCell(getCell10fLeft("Request Topic", true));
        twoColTable2.addCell(getCell10fLeft("Student Name", true));
        twoColTable2.addCell(getCell10fLeft(request.getRequestTopic(), false));
        twoColTable2.addCell(getCell10fLeft(request.getStudentName(), false));

        twoColTable2.addCell(getCell10fLeft("Request Type", true));
        twoColTable2.addCell(getCell10fLeft("Student ID\t\t\tStudent Year", true));
        twoColTable2.addCell(getCell10fLeft(request.getRequestType(), false));
        twoColTable2.addCell(getCell10fLeft(request.getStudentId() + "\t\t\t" + request.getStudentAcademic(), false));
        twoColTable2.addCell(getCell10fLeft("Request Id", true));
        twoColTable2.addCell(getCell10fLeft("Student Faculty\tStudent Department", true));
        twoColTable2.addCell(getCell10fLeft(request.getRequestId(), false));
        twoColTable2.addCell(getCell10fLeft(request.getStudentFaculty() + "\t\t\t" + request.getStudentDepartment(), false));
        twoColTable2.addCell(getCell10fLeft("", true));
        twoColTable2.addCell(getCell10fLeft("Phone Number\tEmail", true));
        twoColTable2.addCell(getCell10fLeft("", false));
        twoColTable2.addCell(getCell10fLeft(String.join("\t\t", request.getStudentPhoneNumber(), request.getStudentEmail()), false));

        document.add(twoColTable2.setFont(font));
        document.add((getCell10fLeft("Reason", true)));
        document.add((getCell10fLeft(request.getRequestNotation(), false)).setFont(font));
        document.add(divider).setBottomMargin(10f);

        Table twoColTable3 = new Table(2);
        twoColTable3.addCell(getHeaderTextCell(request.getRequestTopic()));
        twoColTable3.addCell(new Cell().add(request.getRequestType()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        document.add(twoColTable3.setFont(font));

        // ขนาดของคอลัมน์
        float[] columnWidths = {2, 4, 2, 2, 2}; // กำหนดขนาดของแต่ละคอลัมน์
        Table tableRegis = new Table(UnitValue.createPercentArray(columnWidths));
        tableRegis.setWidth(UnitValue.createPercentValue(100));

        switch (request.getRequestType()) {
            case ("ลงทะเบียนเรียนเกิน 22 หน่วยกิต"),("เพิ่มรายวิชาล่าช้า (Add)"),("ถอนรายวิชาล่าช้า (Drop)"),("ลงทะเบียนเรียนล่าช้า"):
                document.add(getCertifierTable());
                document.add(onesp);
                document.add(onesp);
                document.add(getHeader(request,2));
                document.add(divider).setBottomMargin(10f);

                Table twoColTable4 = new Table(columnWidths);
                twoColTable4.addCell(getHeaderTextCell(request.getRequestTopic()));
                twoColTable4.addCell(new Cell().add(request.getRequestType()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                document.add(twoColTable4.setFont(font));

                tableRegis.addHeaderCell(new Cell().add("รหัสวิชา").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                tableRegis.addHeaderCell(new Cell().add("ชื่อวิชา").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                tableRegis.addHeaderCell(new Cell().add("ประเภท").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                tableRegis.addHeaderCell(new Cell().add("หน่วยเรียน\nบรรยาย\t\tปฏิบัติ").setFontSize(12f).setTextAlignment(TextAlignment.CENTER));
                tableRegis.addHeaderCell(new Cell().add("หน่วยกิต\nบรรยาย\t\tปฏิบัติ").setFontSize(12f).setTextAlignment(TextAlignment.CENTER));
                addSubjectToRegTable(tableRegis, request.getSubjectList());
                document.add(tableRegis);
                break;
            case ("ลาพักการศึกษา"):
                String[] fullSpecial3 = request.getSpecialTopic().split("-");


                if (fullSpecial3 != null && fullSpecial3.length == 2) {
                    String fullText = String.join("\t\t","มีความประสงค์ลาพักการศึกษาเป็นจำนวน " + fullSpecial3[0] +
                            " ภาคการศึกษา  ตั้งแต่ภาค " + fullSpecial3[1] +
                            " ปีการศึกษา " + fullSpecial3[2] +
                            " ถึงภาค " + fullSpecial3[3] +
                            " ปีการศึกษา " + fullSpecial3[4] +
                            " อนึ่ง ข้าพเจ้าได้ลงทะเบียนไว้ในภาค " + fullSpecial3[5] +
                            " ปีการศึกษา " + fullSpecial3[6] +
                            " ดังนี้");
                    document.add(new Cell().add(fullText).setTextAlignment(TextAlignment.CENTER).setFont(font));
                }

                document.add(getCertifierTable());
                document.add(onesp);
                document.add(onesp);
                document.add(getHeader(request,2));
                document.add(divider).setBottomMargin(10f);

                Table twoColTable5 = new Table(2);
                twoColTable5.setWidth(UnitValue.createPercentValue(100)).setHorizontalAlignment(HorizontalAlignment.CENTER);
                twoColTable5.addCell(getHeaderTextCell(request.getRequestTopic()));
                twoColTable5.addCell(new Cell().add(request.getRequestType()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                document.add(twoColTable5.setFont(font));

                Table table = new Table(2);
                table.setWidth(UnitValue.createPercentValue(100));


                table.addHeaderCell(new Cell().add("รหัสวิชา\nCourse code")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));

                table.addHeaderCell(new Cell().add("ชื่ออาจารย์ผู้สอน\nLecturer name")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));

                addSubjectToLeaveOffTable(table, request.getSubjectList());

                document.add(table);
                break;
            case ("ผ่อนผันค่าธรรมเนียมการศึกษา"):
                String[] fullSpecial = request.getSpecialTopic().split("-");
                if (fullSpecial != null && fullSpecial.length == 2) {
                    String fullText = String.join("\t\t","Semester",fullSpecial[0],"Academic Year",fullSpecial[1]);
                    document.add(new Cell().add(fullText).setTextAlignment(TextAlignment.CENTER).setFont(font));
                }
                document.add(getCertifierTable());
                break;
            case ("ย้ายคณะ หรือเปลี่ยนวิชาเอก"):
                String[] fullSpecial2 = request.getSpecialTopic().split("-");
                if (fullSpecial2 != null && fullSpecial2.length == 2) {
                    String fullText = String.join("\t\t","From",fullSpecial2[0],"To",fullSpecial2[1]);
                    document.add(new Cell().add(fullText).setTextAlignment(TextAlignment.CENTER).setFont(font));
                }
                document.add(getCertifierTable());
                break;
            default:
                document.add(getCertifierTable());
                break;
        }

        document.close();
    }

    public void addSubjectToRegTable(Table table, SubjectList subjectList) {
        for (Subject subject: subjectList.getSubjects()){
            table.addCell(new Cell().add(subject.getSubjectId()).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell().add(subject.getSubjectName()).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell().add(subject.getEnrollType()).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell().add(subject.getLectureSection()+"\t\t"+subject.getLabSection()).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell().add(subject.getLectureCredit()+"\t\t"+subject.getLabCredit()).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
    }

    public void addSubjectToLeaveOffTable(Table table, SubjectList subjectList) {
        for (Subject subject: subjectList.getSubjects()){
            table.addCell(new Cell().add(subject.getSubjectId()).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell().add(subject.getSubjectName()).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
    }

    public Table getCertifierTable() {
        String[] boxDetails = {"1) To Head of department", "2) To Dean", "3) Dean's decision"};
        String[] boxRoles = {"Advisor", "Head of Department", "Dean"};

        Table threeColTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1})); // เปลี่ยนจำนวนคอลัมน์เป็น 3
        threeColTable.setWidth(UnitValue.createPercentValue(100)).setHorizontalAlignment(HorizontalAlignment.CENTER);

        ArrayList<Map.Entry<String, String>> timeStampEntries = new ArrayList<>(request.getTimeStampMap().entrySet());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        timeStampEntries.sort((entry1, entry2) -> {
            LocalDateTime date1 = LocalDateTime.parse(entry1.getValue(), formatter);
            LocalDateTime date2 = LocalDateTime.parse(entry2.getValue(), formatter);
            return date1.compareTo(date2);
        });

        int certifiers = timeStampEntries.size(); //asddsa
        boolean advisorApproved = false;
        if (timeStampEntries.size() > 1) {
            advisorApproved = true;
        }
        for (int i = 0; i < 3; i++) {
            Cell cell = new Cell();
            cell.setMinHeight(PageSize.A4.getHeight() / 8); // ความสูงของกล่อง
            cell.setBorder(new SolidBorder(1)); // ใส่เส้นขอบ
            cell.add(new Cell().add(boxDetails[i]).setPaddingRight(30).setTextAlignment(TextAlignment.LEFT));
            if(advisorApproved) {
                    if (timeStampEntries.get(1).getKey().contains("อนุมัติ")) {
                        cell.add("(/)  Approved").setTextAlignment(TextAlignment.CENTER);
                        cell.add("( )      Denied").setTextAlignment(TextAlignment.CENTER);
                    } else if (timeStampEntries.get(1).getKey().contains("ปฏิเสธ")) {
                        cell.add("( )  Approved").setTextAlignment(TextAlignment.CENTER);
                        cell.add("(/)      Denied").setTextAlignment(TextAlignment.CENTER);
                    } else {
                        cell.add("( )  Approved").setTextAlignment(TextAlignment.CENTER);
                        cell.add("( )      Denied").setTextAlignment(TextAlignment.CENTER);
                    }
                    cell.add(new Cell().add("\nSignature " + request.getStudentAdvisor()).setTextAlignment(TextAlignment.RIGHT));
                    cell.add(new Cell().add("(" + request.getStudentAdvisor() + ")").setTextAlignment(TextAlignment.RIGHT));
                    String[] date = request.getRequestDate().split("-");
                    cell.add(new Cell().add(date[0] + "/" + date[1] + "/" + date[2]).setTextAlignment(TextAlignment.RIGHT).setPaddingRight(30));
                advisorApproved = false;
            }

            else {
                cell.add("( )  Approved").setTextAlignment(TextAlignment.CENTER);
                cell.add("( )      Denied").setTextAlignment(TextAlignment.CENTER);
                cell.add(new Cell().add("\nSignature..........................").setTextAlignment(TextAlignment.RIGHT));
                cell.add(new Cell().add("(.................................)").setTextAlignment(TextAlignment.RIGHT));
                cell.add(new Cell().add("....../....../......").setTextAlignment(TextAlignment.RIGHT).setPaddingRight(30));
            }
        }
        return threeColTable;
    }

    public Cell getHeaderTextCell(String textValue){
        return new Cell().add(textValue).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }
    public Cell getHeaderTextCellValue(String textValue){
        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    public Cell getCell10fLeft(String textValue,Boolean isBold){
        Cell myCell = new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return isBold ? myCell.setBold():myCell;
    }

    public Table getHeader(Request request,int pageNumber){
        Table table = new Table(twocolmnWidth);
        table.addCell(new Cell().add("ใบคำร้อง").setFontSize(40f).setBorder(Border.NO_BORDER).setBold().setVerticalAlignment(VerticalAlignment.MIDDLE));
        Table nestedTable = new Table(new float[]{twocol / 3, twocol / 3,twocol/3});
        nestedTable.addCell(getHeaderTextCell("Request No."));
        nestedTable.addCell(getHeaderTextCellValue(request.getRequestId()));
        if (request.getRequestType().equals("ลงทะเบียนเรียนเกิน 22 หน่วยกิต") ||
                request.getRequestType().equals("ลงทะเบียนเรียนล่าช้า") ||
                request.getRequestType().equals("เพิ่มรายวิชาล่าช้า (Add)") ||
                request.getRequestType().equals("ถอนรายวิชาล่าช้า (Drop)") ||
                request.getRequestType().equals("ลาพักการศึกษา")){
            nestedTable.addCell(getHeaderTextCell("Page ( "+pageNumber+"/2 )"));
        }else {
            nestedTable.addCell(getHeaderTextCell("Page ( 1/1 )"));
        }

        nestedTable.addCell(getHeaderTextCell("Request Date."));
        nestedTable.addCell(getHeaderTextCellValue(request.getRequestLastedDated()));

        table.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));

        return table;
    }
}
