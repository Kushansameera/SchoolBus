package com.example.kusha.schoolbus.application;



import com.example.kusha.schoolbus.models.PaymentSummery;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kusha on 1/8/2017.
 */

public class ReportCreator {
    private String FILE = null;
    private PaymentSummery paymentSummery;
    private static Font mcatFont;
    private static Font catFont;
    private static Font smallBold;
    private static Font normalBold;
    private static Font tableHeader;
    private static Font tableBody;
    private Image imgLogo;
    private Image imgChart;
    private static BaseColor colorPrimaryDark, colorPrimary, colorPrimaryLight, textColorPrimary;

    public ReportCreator(String FILE, PaymentSummery paymentSummery) {
        this.FILE = FILE;
        this.paymentSummery = paymentSummery;
        colorPrimaryDark = new BaseColor(2, 37, 179);
        colorPrimary = new BaseColor(56, 95, 252);
        colorPrimaryLight = new BaseColor(56, 95, 252);
        textColorPrimary = new BaseColor(255, 255, 255);
        mcatFont = new Font(Font.FontFamily.TIMES_ROMAN, 25,
                Font.BOLD, colorPrimaryDark);
        catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                Font.BOLD, colorPrimaryDark);
        smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 9,
                Font.BOLD, colorPrimaryLight);
        smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.BOLD, colorPrimary);
        tableHeader = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.BOLD, textColorPrimary);
        tableBody = new Font(Font.FontFamily.TIMES_ROMAN, 11,
                Font.NORMAL, colorPrimary);
    }

    public void createPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();
            addMetaData(document);
            addContent(document);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMetaData(Document document) {
        document.addTitle("Payment Summery");
        document.addSubject("School Bus");
        document.addKeywords("School Bus, Payment, Summery");
        document.addAuthor("School Bus Developer");
        document.addCreator("School Bus Developer");
    }

    private void addContent(Document document) throws DocumentException, IOException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);
        Paragraph report = new Paragraph();
        imgLogo.scaleAbsolute(48,48);
        report.add(imgLogo);
        report.add(new Paragraph("\t\t\t\t\t\tSchool Bus",mcatFont));
        addEmptyLine(report,2);
        report.add(new Paragraph("\t\t\tPayment Summery for "+paymentSummery.getYear()+"/"+paymentSummery.getMonth(), catFont));
        report.add(new Paragraph("" +s, smallBold));
        addEmptyLine(report,2);
        report.add(new Paragraph("Target Income : "+paymentSummery.getTargetIncome(), smallBold));
        addEmptyLine(report,1);
        report.add(new Paragraph("Received           : "+paymentSummery.getReceived(), smallBold));
        addEmptyLine(report,1);
        report.add(new Paragraph("Receivables      : "+paymentSummery.getReceivables(), smallBold));
        addEmptyLine(report,1);
        document.add(report);

        imgChart.scaleAbsolute(263,263);
        document.add(imgChart);

    }
    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void setImgLogo(Image imgLogo) {
        this.imgLogo = imgLogo;
    }

    public void setImgChart(Image imgChart) {
        this.imgChart = imgChart;
    }
}
