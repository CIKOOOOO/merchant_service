package com.andrew.prototype.Model;

public class Report {
    private String report_title;
    private boolean report_checked;

    public String getReport_title() {
        return report_title;
    }

    public void setReport_checked(boolean report_checked) {
        this.report_checked = report_checked;
    }

    public boolean isReport_checked() {
        return report_checked;
    }

    public Report(String report_title, boolean report_checked) {

        this.report_title = report_title;
        this.report_checked = report_checked;
    }
}
