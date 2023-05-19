package pl.junkiewiczd;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.junkiewiczd.reports.PDFReportsService;
import pl.junkiewiczd.reports.PeriodOfTime;
import pl.junkiewiczd.tables.HostRepository;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class PDFReportsController {

    private final PDFReportsService pdfReportsService;

    public PDFReportsController(PDFReportsService pdfReportsService) {
        this.pdfReportsService = pdfReportsService;
    }

    @GetMapping("/report1")
    public void generatePDFReport(HttpServletResponse response, @RequestBody PeriodOfTime periodOfTime) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "inline; filename=pdf_"+ currentDateTime +".pdf";
        response.setHeader(headerKey, headerValue);

        this.pdfReportsService.export(response, periodOfTime);
    }
}
