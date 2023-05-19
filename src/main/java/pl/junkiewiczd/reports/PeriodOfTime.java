package pl.junkiewiczd.reports;

import java.time.LocalDate;

public class PeriodOfTime {
    private LocalDate beginning;
    private LocalDate end;

    public PeriodOfTime(LocalDate beginning, LocalDate end) {
        this.beginning = beginning;
        this.end = end;
    }

    public LocalDate getBeginning() {
        return beginning;
    }

    public LocalDate getEnd() {
        return end;
    }
}
