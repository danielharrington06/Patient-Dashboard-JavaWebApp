package uk.ac.ucl.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

@WebServlet("/exportJSON")
public class ExportJSONServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Model model = ModelFactory.getModel();
        try {
            model.exportToJSON();
            request.setAttribute("successMessage", "Data exported to JSON successfully.");
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Failed to export JSON: " + e.getMessage());
        }
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
