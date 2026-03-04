package uk.ac.ucl.servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.model.Patient;

/**
 * The ViewPatientListServlet handles HTTP requests for displaying the full list of patients.
 * It is mapped to the URL "/patientList".
 *
 * This servlet demonstrates:
 * 1. Handling GET requests to retrieve and display data.
 * 2. Interacting with a Model via a Factory pattern.
 * 3. Error handling and forwarding to error pages.
 * 4. Request-scoped attribute passing to JSPs for rendering lists.
 */
@WebServlet("/patientRecord")
public class ViewPatientInfoServlet extends HttpServlet
{

    /**
     * Handles HTTP GET requests.
     * This is the primary method for retrieving the patient list.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made of the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the GET could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        try {
            // Get the singleton instance of the Model.
            // The Model handles the actual data processing and data retrieval.
            Model model = ModelFactory.getModel();

            // get id from URL
            String id = request.getParameter("id");

            // stores patient info in a patient object
            Patient patientInfo = model.getPatientRecord(id);

            // NEW: get column names for displaying with data
            List<String> columnNames = model.getColumnNames();

            // Add attributes for JSP
            request.setAttribute("patientInfo", patientInfo);
            request.setAttribute("columnNames", columnNames);

            RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/patientRecord.jsp");

            dispatch.forward(request, response);

        } catch (IOException e) {
            // Exception Handling.
            // If there is an issue loading the model or data, log the error and forward to a dedicated error page.
            request.setAttribute("errorMessage", "Error loading data: " + e.getMessage());
            ServletContext context = getServletContext();
            RequestDispatcher dispatch = context.getRequestDispatcher("/error.jsp");
            dispatch.forward(request, response);
        }
    }

    /**
     * Handles HTTP POST requests.
     * Redirects to doGet as viewing a list is typically an idempotent operation.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made of the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the POST could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the POST request
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
