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
 * The SearchServlet handles HTTP requests for performing patient searches.
 * It is mapped to the URL "/runsearch".
 *
 * This servlet demonstrates:
 * 1. Handling both GET and POST requests.
 * 2. Interacting with a Model via a Factory pattern.
 * 3. Input validation.
 * 4. Error handling and forwarding to error pages.
 * 5. Request-scoped attribute passing to JSPs for rendering results.
 */
@WebServlet("/runsearch")
public class SearchServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests.
     *
     * By calling doPost, this allows search results to be bookmarked and refreshed
     * (since many browsers default to GET for URL-based navigation).
     *
     * @param request  the HttpServletRequest object that contains the request the client has made of the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the GET could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Handles HTTP POST requests.
     * This is where the core search logic resides.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made of the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the POST could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the POST request
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchString = request.getParameter("searchstring");

        try {
            Model model = ModelFactory.getModel();

            if (searchString == null || searchString.trim().isEmpty()) {
                response.sendRedirect("/patientList");
                return;
            }

            Map<String, List<String>> results = model.searchPatientSummaries(searchString);

            String sortKey = request.getParameter("sort");
            String sortDir = request.getParameter("dir");
            boolean ascending = !"desc".equals(sortDir);

            if (sortKey != null && !sortKey.isEmpty()) {
                results = model.sortPatientSummaries(results, sortKey, ascending);
            }

            int pageSize = Model.DEFAULT_PAGE_SIZE;
            int page = 1;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null) page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }

            int totalPatients = results.size();
            int totalPages = (int) Math.ceil((double) totalPatients / pageSize);

            Map<String, List<String>> pageData = model.getPage(results, page, pageSize);

            request.setAttribute("sortKey", sortKey);
            request.setAttribute("sortDir", sortDir);

            request.setAttribute("patientData", pageData);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalPatients", totalPatients);
            request.setAttribute("columnDisplayNames", model.getSummaryColumnDisplayNames());

            ServletContext context = getServletContext();
            RequestDispatcher dispatch = context.getRequestDispatcher("/patientList.jsp");
            dispatch.forward(request, response);

        } catch (IOException e) {
            request.setAttribute("errorMessage", "Error loading data: " + e.getMessage());
            ServletContext context = getServletContext();
            RequestDispatcher dispatch = context.getRequestDispatcher("/error.jsp");
            dispatch.forward(request, response);
        }
    }
}
