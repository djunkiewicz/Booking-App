package pl.junkiewiczd.reports;

import com.lowagie.text.*;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.junkiewiczd.basicobjects.apartments.ApartmentReportDetails;
import pl.junkiewiczd.tables.ApartmentRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PDFReportsService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    public void export(HttpServletResponse response, PeriodOfTime periodOfTime) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        List<ApartmentReportDetails> apartmentReportDetails = apartmentRepository.getReportDetails(periodOfTime);

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        Paragraph titleParagraph = new Paragraph("REPORT\n\n", fontTitle);
        titleParagraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(12);
        Paragraph paragraph1 = new Paragraph("  Please see below booking details for each apartment" +
                " from " + periodOfTime.getBeginning() + " to " + periodOfTime.getEnd() + ":");
        paragraph1.setAlignment(Paragraph.ALIGN_LEFT);

        float[] columnWidths = {1, 6, 2, 2};
        Table reportTable = new Table(4);
        reportTable.setWidths(columnWidths);
        reportTable.setPadding(5);

        String[] headerCellNames = {"RN", "Apartment name", "Amount of reservations", "Days of reservations"};
        for (String cellName : headerCellNames) {
            Cell cell = new Cell(cellName);
            cell.setVerticalAlignment(VerticalAlignment.CENTER);
            cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
            reportTable.addCell(cell);
        }

        AtomicInteger i = new AtomicInteger(1);
        VerticalAlignment v = VerticalAlignment.CENTER;
        HorizontalAlignment h = HorizontalAlignment.CENTER;

        apartmentReportDetails.stream()
                .sorted(Comparator.comparing(ApartmentReportDetails::getDaysOfReservation, Comparator.reverseOrder()))
                .toList()
                .forEach(singleRow -> {
                    reportTable.addCell(createCellWithAlignment(String.valueOf(i), v, h));
                    reportTable.addCell(new Cell(singleRow.getName()));
                    reportTable.addCell(createCellWithAlignment(String.valueOf(singleRow.getAmountOfReservation()), v, h));
                    reportTable.addCell(createCellWithAlignment(String.valueOf(singleRow.getDaysOfReservation()), v, h));
                    i.getAndIncrement();
                });


        document.add(titleParagraph);
        document.add(paragraph1);
        document.add(reportTable);


        document.close();
    }

    private Cell createCellWithAlignment(String text, VerticalAlignment v, HorizontalAlignment h) {
        Cell cell = new Cell(text);
        cell.setVerticalAlignment(v);
        cell.setHorizontalAlignment(h);
        return cell;
    }

}
