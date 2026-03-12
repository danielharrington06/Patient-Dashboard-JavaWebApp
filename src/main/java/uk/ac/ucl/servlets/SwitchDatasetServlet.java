package uk.ac.ucl.servlets;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

@WebServlet("/switchdataset")
public class SwitchDatasetServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String size = request.getParameter("datasize");
        List<String> valid = List.of("100", "1000", "10000", "100000");
        if (valid.contains(size)) {
            Model model = ModelFactory.getModel();
            model.reloadData(Paths.get("data", "patients" + size + ".csv").toString());
        }
        response.sendRedirect("/patientList");
    }
}
