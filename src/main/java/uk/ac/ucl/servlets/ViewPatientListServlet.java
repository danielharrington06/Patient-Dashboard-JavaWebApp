package uk.ac.ucl.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

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
@WebServlet("/patientList")
public class ViewPatientListServlet extends HttpServlet
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
        Model model = ModelFactory.getModel();
        Map<String, List<String>> allPatients = model.getPatientSummaries();

        int pageSize = Model.DEFAULT_PAGE_SIZE;
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) page = Integer.parseInt(pageParam);
        } catch (NumberFormatException e) {
            page = 1;
        }

        int totalPatients = allPatients.size();
        int totalPages = (int) Math.ceil((double) totalPatients / pageSize);

        Map<String, List<String>> pageData = model.getPage(allPatients, page, pageSize);

        request.setAttribute("patientData", pageData);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalPatients", totalPatients);
        request.setAttribute("columnDisplayNames", model.getSummaryColumnDisplayNames());

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/patientList.jsp");
        dispatch.forward(request, response);
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
