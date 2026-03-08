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

@WebServlet("/runsearch")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchString = request.getParameter("searchstring");

        try {
            Model model = ModelFactory.getModel();

            // 1. Get filter parameters
            String genderFilter = request.getParameter("gender");
            String aliveFilter = request.getParameter("alive");
            String maritalFilter = request.getParameter("marital");
            String raceFilter = request.getParameter("race");
            String ethnicityFilter = request.getParameter("ethnicity");

            // 2. If no search and no filters, redirect to full list
            boolean hasSearch = searchString != null && !searchString.trim().isEmpty();
            boolean hasFilters = (genderFilter != null && !genderFilter.isEmpty())
                              || (aliveFilter != null && !aliveFilter.isEmpty())
                              || (maritalFilter != null && !maritalFilter.isEmpty())
                              || (raceFilter != null && !raceFilter.isEmpty())
                              || (ethnicityFilter != null && !ethnicityFilter.isEmpty());

            if (!hasSearch && !hasFilters) {
                response.sendRedirect("/patientList");
                return;
            }

            // 3. Search (or get all if no search term but filters are present)
            Map<String, List<String>> results = hasSearch
                ? model.searchPatientSummaries(searchString)
                : model.getPatientSummaries();

            // 4. Apply filters
            results = model.filterPatients(results, genderFilter, aliveFilter, maritalFilter, raceFilter, ethnicityFilter);

            // 5. Sort
            String sortKey = request.getParameter("sort");
            String sortDir = request.getParameter("dir");
            boolean ascending = !"desc".equals(sortDir);

            if (sortKey != null && !sortKey.isEmpty()) {
                results = model.sortPatientSummaries(results, sortKey, ascending);
            }

            // 6. Paginate
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

            // 7. Set attributes
            request.setAttribute("patientData", pageData);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalPatients", totalPatients);
            request.setAttribute("columnDisplayNames", model.getSummaryColumnDisplayNames());
            request.setAttribute("sortKey", sortKey);
            request.setAttribute("sortDir", sortDir);
            request.setAttribute("genderFilter", genderFilter);
            request.setAttribute("aliveFilter", aliveFilter);
            request.setAttribute("maritalFilter", maritalFilter);
            request.setAttribute("raceFilter", raceFilter);
            request.setAttribute("ethnicityFilter", ethnicityFilter);
            request.setAttribute("raceOptions", model.getDistinctValuesWithLabels("RACE"));
            request.setAttribute("ethnicityOptions", model.getDistinctValuesWithLabels("ETHNICITY"));

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