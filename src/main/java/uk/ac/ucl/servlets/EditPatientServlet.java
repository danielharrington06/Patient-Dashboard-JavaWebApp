package uk.ac.ucl.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

@WebServlet("/editPatient")
public class EditPatientServlet extends HttpServlet {

    private static final String[] COLUMNS = {
        "BIRTHDATE", "DEATHDATE", "SSN", "DRIVERS", "PASSPORT",
        "PREFIX", "FIRST", "LAST", "SUFFIX", "MAIDEN",
        "MARITAL", "RACE", "ETHNICITY", "GENDER", "BIRTHPLACE",
        "ADDRESS", "CITY", "STATE", "ZIP"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        try {
            Model model = ModelFactory.getModel();
            Map<String, String> rawRecord = model.getRawPatientRecord(id);
            request.setAttribute("rawRecord", rawRecord);
            request.setAttribute("patientId", id);
            request.setAttribute("from", request.getParameter("from"));
            getServletContext().getRequestDispatcher("/editPatient.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Patient not found: " + e.getMessage());
            getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String from = request.getParameter("from");

        // Collect values
        Map<String, String> values = new LinkedHashMap<>();
        for (String col : COLUMNS) {
            String val = request.getParameter(col);
            values.put(col, val != null ? val.trim() : "");
        }

        // Validate
        StringBuilder errors = new StringBuilder();

        if (values.get("FIRST").isEmpty())     errors.append("First name is required. ");
        if (values.get("LAST").isEmpty())      errors.append("Last name is required. ");
        if (values.get("GENDER").isEmpty())    errors.append("Gender is required. ");
        if (values.get("SSN").isEmpty())       errors.append("SSN is required. ");
        if (values.get("BIRTHDATE").isEmpty()) errors.append("Date of birth is required. ");

        String ssn = values.get("SSN");
        if (!ssn.isEmpty() && !ssn.matches("\\d{3}-\\d{2}-\\d{4}")) {
            errors.append("SSN must be in format 999-12-3456. ");
        }

        String zip = values.get("ZIP");
        if (!zip.isEmpty() && !zip.matches("\\d{5}")) {
            errors.append("ZIP code must be 5 digits");
        }

        LocalDate today = LocalDate.now();
        LocalDate birthDate = null;

        String birthdateStr = values.get("BIRTHDATE");
        if (!birthdateStr.isEmpty()) {
            try {
                birthDate = LocalDate.parse(birthdateStr);
                if (birthDate.isAfter(today))
                    errors.append("Date of birth cannot be in the future. ");
                if (birthDate.isBefore(LocalDate.of(1900, 1, 1)))
                    errors.append("Date of birth must be on or after 1900-01-01. ");
            } catch (DateTimeParseException e) {
                errors.append("Date of birth is not a valid date. ");
            }
        }

        String deathdateStr = values.get("DEATHDATE");
        if (!deathdateStr.isEmpty()) {
            try {
                LocalDate deathDate = LocalDate.parse(deathdateStr);
                if (deathDate.isAfter(today))
                    errors.append("Date of death cannot be in the future. ");
                if (birthDate != null && deathDate.isBefore(birthDate))
                    errors.append("Date of death cannot be before date of birth. ");
            } catch (DateTimeParseException e) {
                errors.append("Date of death is not a valid date. ");
            }
        }

        // If invalid, bounce back with errors and preserved values
        if (errors.length() > 0) {
            request.setAttribute("errorMessage", errors.toString().trim());
            request.setAttribute("patientId", id);
            request.setAttribute("from", from);
            request.setAttribute("rawRecord", new java.util.LinkedHashMap<>(values)); // fix
            getServletContext().getRequestDispatcher("/editPatient.jsp").forward(request, response);
            return;
        }

        // Save
        try {
            Model model = ModelFactory.getModel();
            model.editPatient(id, values);
            response.sendRedirect("/patientRecord?id=" + id + (from != null && !from.isEmpty() ? "&from=" + from : ""));
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Failed to save patient: " + e.getMessage());
            request.setAttribute("patientId", id);
            request.setAttribute("from", from);
            request.setAttribute("rawRecord", new java.util.LinkedHashMap<>(values)); // fix
            getServletContext().getRequestDispatcher("/editPatient.jsp").forward(request, response);
        }
    }
}