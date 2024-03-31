package rippling.reporting;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String name;
    private int rating;
    private List<Employee> reports;

    public Employee(String name, int rating) {
        this.name = name;
        this.rating = rating;
        this.reports = new ArrayList<>();
    }

    public void addReport(Employee employee) {
        this.reports.add(employee);
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public List<Employee> getReports() {
        return reports;
    }
}
