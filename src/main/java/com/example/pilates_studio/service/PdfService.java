package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.AppointmentDto;
import com.example.pilates_studio.dto.PurchaseDto;
import com.example.pilates_studio.dto.TrainerDto;
import com.example.pilates_studio.model.Purchase;
import com.example.pilates_studio.model.Trainer;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PdfService {

    @Autowired
    AppointmentService appointmentService;
    @Autowired
    TrainerService trainerService;
    @Autowired
    CustomerService customerService;
    @Autowired
    PurchaseService purchaseService;


    public ByteArrayInputStream generateRoomSchedulePdf(String franchise, String room, LocalDate date) {
        List<AppointmentDto> appointments = appointmentService.findAppointmentsByFranchiseAmdRoomAndDate(franchise, room, date);
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try{
            PdfWriter.getInstance(document, out);
            document.open();

            Image logo = Image.getInstance("src/main/resources/static/assets/img/logo.jpg");
            logo.setAbsolutePosition(30, 770);
            logo.scaleToFit(50, 50);
            document.add(logo);

            Font dateTimeFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph dateTime = new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")), dateTimeFont);
            dateTime.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateTime);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Room Schedule for " + franchise + "-" + room + " on " + date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));
            document.add(title);

            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{50, 200, 200, 175, 95, 95, 170});

            Stream.of("Id", "Customer Name", "Trainer Name", "Location", "Start Time", "End Time", "Status")
                            .forEach(headerTitle -> {
                                PdfPCell headerCell = new PdfPCell();
                                headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                headerCell.setPadding(5);
                                Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
                                headerCell.setPhrase(new Phrase(headerTitle, headFont));
                                table.addCell(headerCell);
                            });

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            for (AppointmentDto appointment : appointments) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(appointment.getId().toString(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getCustomerName(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getTrainerName(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getLocationId(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getStartTime().format(formatter), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getEndTime().format(formatter), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getAppointmentStatus().toString(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);
            }

            document.add(table);
            document.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateTrainerSchedulePdf(String trainer, LocalDate date) {
        List<AppointmentDto> appointments = appointmentService.findAppointmentsByTrainerIdAndDate(trainer, date);
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try{
            PdfWriter.getInstance(document, out);
            document.open();

            Image logo = Image.getInstance("src/main/resources/static/assets/img/logo.jpg");
            logo.setAbsolutePosition(30, 770);
            logo.scaleToFit(50, 50);
            document.add(logo);

            Font dateTimeFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph dateTime = new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")), dateTimeFont);
            dateTime.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateTime);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Schedule for " + trainerService.findTrainerById(Integer.parseInt(trainer)).getName() + " on " + date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));
            document.add(title);

            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{50, 200, 150, 95, 95, 120});

            Stream.of("Id", "Customer Name", "Location", "Start Time", "End Time", "Status")
                    .forEach(headerTitle -> {
                        PdfPCell headerCell = new PdfPCell();
                        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        headerCell.setPadding(5);
                        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
                        headerCell.setPhrase(new Phrase(headerTitle, headFont));
                        table.addCell(headerCell);
                    });


            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            for (AppointmentDto appointment : appointments) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(appointment.getId().toString(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getCustomerName(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getLocationId(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getStartTime().format(formatter), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getEndTime().format(formatter), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getAppointmentStatus().toString(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);
            }

            document.add(table);
            document.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateCustomerPackageDetailsPdf(Integer customerId){
        List<AppointmentDto> appointments = appointmentService.findPackageAppointmentsByCustomerId(customerId);
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try{
            PdfWriter.getInstance(document, out);
            document.open();

            Image logo = Image.getInstance("src/main/resources/static/assets/img/logo.jpg");
            logo.setAbsolutePosition(30, 770);
            logo.scaleToFit(50, 50);
            document.add(logo);

            Font dateTimeFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph dateTime = new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")), dateTimeFont);
            dateTime.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateTime);

            Font purchaseFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Integer intPurchaseId = purchaseService.findInUsePurchaseIdByCustomerId(customerId);
            if (intPurchaseId == null) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                Font errorFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED);
                Paragraph errorMessage = new Paragraph("Error: Purchase not found for the given customer: " + customerService.findCustomerById(customerId).getName(), errorFont);
                errorMessage.setAlignment(Element.ALIGN_CENTER);
                document.add(errorMessage);
                document.close();
                return new ByteArrayInputStream(out.toByteArray());
            }
            PurchaseDto purchase = purchaseService.findPurchaseById(intPurchaseId);
            Paragraph lessonCount = new Paragraph("Lesson Count: " + purchase.getLessonCount(), purchaseFont);
            lessonCount.setAlignment(Element.ALIGN_RIGHT);
            document.add(lessonCount);

            Paragraph purchaseId = new Paragraph("Purchase Id: " + purchaseService.findInUsePurchaseIdByCustomerId(customerId), purchaseFont);
            purchaseId.setAlignment(Element.ALIGN_RIGHT);
            document.add(purchaseId);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Package Details for " + customerService.findCustomerById(customerId).getName(), headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));
            document.add(title);

            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{20, 30, 100, 90, 70, 70, 100});

            Stream.of("No", "Id", "Trainer Name", "Location", "Start Time", "End Time", "Status")
                    .forEach(headerTitle -> {
                        PdfPCell headerCell = new PdfPCell();
                        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        headerCell.setPadding(5);
                        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
                        headerCell.setPhrase(new Phrase(headerTitle, headFont));
                        table.addCell(headerCell);
                    });


            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm");
            int counter = 1;

            for(int i = 0; i < appointments.size(); i++){
                AppointmentDto appointment = appointments.get(i);
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(String.valueOf(i + 1), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getId().toString(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getTrainerName(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getLocationId(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getStartTime().format(formatter), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getEndTime().format(formatter), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getAppointmentStatus().toString(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);
                counter++;
            }

            for(int i = counter; i <= purchase.getLessonCount(); i++){
                PdfPCell NumberCell = new PdfPCell(new Phrase(String.valueOf(i), cellFont));
                NumberCell.setPadding(5);
                table.addCell(NumberCell);
                for(int j = 0; j < 6; j++){
                    PdfPCell cell = new PdfPCell(new Phrase(" ", cellFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateCustomerPackageDetailsPdfByPurchaseId(Integer purchaseId){
        List<AppointmentDto> appointments = appointmentService.findPackageAppointmentsByPurchaseId(purchaseId);
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try{
            PdfWriter.getInstance(document, out);
            document.open();

            Image logo = Image.getInstance("src/main/resources/static/assets/img/logo.jpg");
            logo.setAbsolutePosition(30, 770);
            logo.scaleToFit(50, 50);
            document.add(logo);

            Font dateTimeFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph dateTime = new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")), dateTimeFont);
            dateTime.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateTime);


            if(!purchaseService.existsByPurchaseId(purchaseId)){
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                Font errorFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED);
                Paragraph errorMessage = new Paragraph("Error: Purchase not found for the given purchase ID: " + purchaseId, errorFont);
                errorMessage.setAlignment(Element.ALIGN_CENTER);
                document.add(errorMessage);
                document.close();
                return new ByteArrayInputStream(out.toByteArray());
            }

            PurchaseDto purchaseDto = purchaseService.findPurchaseById(purchaseId);
            Paragraph lessonCount = new Paragraph("Lesson Count: " + purchaseDto.getLessonCount(), dateTimeFont);
            lessonCount.setAlignment(Element.ALIGN_RIGHT);
            document.add(lessonCount);

            Paragraph purchaseIdParagraph = new Paragraph("Purchase Id: " + purchaseId, dateTimeFont);
            purchaseIdParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(purchaseIdParagraph);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Package Details for " + purchaseDto.getCustomerName(), headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" "));
            document.add(title);

            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{20, 30, 100, 90, 70, 70, 100});

            Stream.of("No", "Id", "Trainer Name", "Location", "Start Time", "End Time", "Status")
                    .forEach(headerTitle -> {
                        PdfPCell headerCell = new PdfPCell();
                        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        headerCell.setPadding(5);
                        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
                        headerCell.setPhrase(new Phrase(headerTitle, headFont));
                        table.addCell(headerCell);
                    });

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm");
            int counter = 1;

            for(int i = 0; i < appointments.size(); i++){
                AppointmentDto appointment = appointments.get(i);
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(String.valueOf(i + 1), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getId().toString(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getTrainerName(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getLocationId(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getStartTime().format(formatter), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getEndTime().format(formatter), cellFont));
                cell.setPadding(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(appointment.getAppointmentStatus().toString(), cellFont));
                cell.setPadding(5);
                table.addCell(cell);
                counter++;
            }

            for(int i = counter; i <= purchaseDto.getLessonCount(); i++){
                PdfPCell NumberCell = new PdfPCell(new Phrase(String.valueOf(i), cellFont));
                NumberCell.setPadding(5);
                table.addCell(NumberCell);
                for(int j = 0; j < 6; j++){
                    PdfPCell cell = new PdfPCell(new Phrase(" ", cellFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
