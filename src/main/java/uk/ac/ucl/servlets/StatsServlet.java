package uk.ac.ucl.servlets;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.JSONWriter;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

@WebServlet("/stats")
public class StatsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Model model = ModelFactory.getModel();

        // Key numbers
        request.setAttribute("totalPatients",       model.getTotalPatients());
        request.setAttribute("oldestAliveAge",       model.getOldestAliveAge());
        request.setAttribute("youngestAliveAge",     model.getYoungestAliveAge());
        request.setAttribute("averageAliveAge",      String.format("%.1f", model.getAverageAliveAge()));
        request.setAttribute("averageAgeAtDeath",    String.format("%.1f", model.getAverageAgeAtDeath()));
        String[] topCity = model.getMostCommonCity();
        request.setAttribute("mostCommonCity",       topCity[0]);
        request.setAttribute("mostCommonCityCount",  topCity[1]);
        request.setAttribute("mostCommonEthnicity",  model.getMostCommonEthnicity());

        // Chart data (passed as maps; JSP converts to JSON arrays)
        String[] genderJson    = JSONWriter.mapChartToJson(model.getGenderCounts());
        String[] maritalJson   = JSONWriter.mapChartToJson(model.getMaritalCounts());
        String[] ethnicityJson = JSONWriter.mapChartToJson(model.getEthnicityCounts(Model.ETHNICITY_CHART_TOP_N));
        String[] raceJson      = JSONWriter.mapChartToJson(model.getRaceCounts());

        request.setAttribute("genderLabels",    genderJson[0]);
        request.setAttribute("genderValues",    genderJson[1]);
        request.setAttribute("maritalLabels",   maritalJson[0]);
        request.setAttribute("maritalValues",   maritalJson[1]);
        request.setAttribute("ethnicityLabels", ethnicityJson[0]);
        request.setAttribute("ethnicityValues", ethnicityJson[1]);
        request.setAttribute("raceLabels",      raceJson[0]);
        request.setAttribute("raceValues",      raceJson[1]);
        request.setAttribute("ethnicityTopN", Model.ETHNICITY_CHART_TOP_N);

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/stats.jsp");
        dispatch.forward(request, response);
    }
}