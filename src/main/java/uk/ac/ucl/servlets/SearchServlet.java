package uk.ac.ucl.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import uk.ac.ucl.util.URLBuilder;

@WebServlet("/runsearch")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Model model = ModelFactory.getModel();
            request.setAttribute("activePage", "patients");

            // Read search term
            String searchString = request.getParameter("searchstring");

            // Read filter parameters
            String genderFilter = request.getParameter("gender");
            String aliveFilter = request.getParameter("alive");
            String maritalFilter = request.getParameter("marital");
            String[] raceFilters = request.getParameterValues("race");
            String[] ethnicityFilters = request.getParameterValues("ethnicity");
            List<String> raceFilterList = raceFilters != null ? Arrays.asList(raceFilters) : new ArrayList<>();
            List<String> ethnicityFilterList = ethnicityFilters != null ? Arrays.asList(ethnicityFilters) : new ArrayList<>();

            // Redirect if nothing to search or filter
            boolean hasSearch = searchString != null && !searchString.trim().isEmpty();
            boolean hasFilters = (genderFilter != null && !genderFilter.isEmpty())
                              || (aliveFilter != null && !aliveFilter.isEmpty())
                              || (maritalFilter != null && !maritalFilter.isEmpty())
                              || !raceFilterList.isEmpty()
                              || !ethnicityFilterList.isEmpty();

            if (!hasSearch && !hasFilters) {
                response.sendRedirect("/patientList");
                return;
            }

            // Fetch data (search or full list)
            Map<String, List<String>> results = hasSearch
                ? model.searchPatientSummaries(searchString)
                : model.getPatientSummaries();

            // Apply filters
            results = model.filterPatients(results, genderFilter, aliveFilter, maritalFilter, raceFilterList, ethnicityFilterList);

            // Apply sort
            String sortKey = request.getParameter("sort");
            String sortDir = request.getParameter("dir");
            boolean ascending = !"desc".equals(sortDir);

            if (sortKey != null && !sortKey.isEmpty()) {
                results = model.sortPatientSummaries(results, sortKey, ascending);
            }

            // Paginate
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

            // Set request attributes for JSP
            request.setAttribute("patientData", pageData);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalPatients", totalPatients);
            request.setAttribute("columnDisplayNames", model.getSummaryColumnNamesFormatted());
            request.setAttribute("sortKey", sortKey);
            request.setAttribute("sortDir", sortDir);
            request.setAttribute("genderFilter", genderFilter);
            request.setAttribute("aliveFilter", aliveFilter);
            request.setAttribute("maritalFilter", maritalFilter);
            request.setAttribute("raceFilterList", raceFilterList);
            request.setAttribute("ethnicityFilterList", ethnicityFilterList);
            request.setAttribute("raceOptions", model.getDistinctValuesMapInCol("RACE"));
            request.setAttribute("ethnicityOptions", model.getDistinctValuesMapInCol("ETHNICITY"));
            request.setAttribute("resetUrls", URLBuilder.buildResetUrls(
                searchString, genderFilter, aliveFilter, maritalFilter, raceFilterList, ethnicityFilterList
            ));
            request.setAttribute("paginationBaseUrl", URLBuilder.buildPaginationBaseUrl(
                searchString, genderFilter, aliveFilter, maritalFilter, raceFilterList, ethnicityFilterList, sortKey, sortDir
            ));

            // Save current list URL to session for back navigation
            String queryString = request.getQueryString();
            String currentUrl = "/runsearch" + (queryString != null ? "?" + queryString : "");
            request.getSession().setAttribute("lastListUrl", currentUrl);

            // Forward to JSP
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