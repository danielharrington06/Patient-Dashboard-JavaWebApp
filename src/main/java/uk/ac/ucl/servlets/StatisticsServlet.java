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

@WebServlet("/statistics")
public class StatisticsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Model model = ModelFactory.getModel();

        // Key numbers
        request.setAttribute("totalPatients",       model.getRowCount());
        request.setAttribute("oldestAliveAge",      model.getOldestAliveAge());
        request.setAttribute("youngestAliveAge",    model.getYoungestAliveAge());
        request.setAttribute("averageAliveAge",      String.format("%.1f", model.getAverageAliveAge()));
        request.setAttribute("averageAgeAtDeath",   String.format("%.1f", model.getAverageAgeAtDeath()));
        request.setAttribute("livingCount",         model.getLivingCount());
        request.setAttribute("distinctCityCount",   model.getDistinctCityCount());
        String[] topCity = model.getMostCommonCity();
        request.setAttribute("mostCommonCity",      topCity[0]);
        request.setAttribute("mostCommonCityCount", topCity[1]);
        request.setAttribute("mostCommonEthnicity", model.getMostCommonEthnicity());
        request.setAttribute("mostCommonRace",      model.getMostCommonRace());
        request.setAttribute("ethnicityTopN",       Model.ETHNICITY_CHART_TOP_N);
        request.setAttribute("cityTopN",            Model.CITY_CHART_TOP_N);

        // Chart data
        String chartDataJson = JSONWriter.buildChartDataJson(
            JSONWriter.mapChartToJson(model.getGenderCounts()),
            JSONWriter.mapChartToJson(model.getMaritalCounts()),
            JSONWriter.mapChartToJson(model.getEthnicityCounts(Model.ETHNICITY_CHART_TOP_N)),
            JSONWriter.mapChartToJson(model.getRaceCounts()),
            JSONWriter.mapChartToJson(model.getLivingDeceasedCounts()),
            JSONWriter.mapChartToJson(model.getCityCounts(Model.CITY_CHART_TOP_N)),
            JSONWriter.mapChartToJson(model.getAliveAgeHistogram())
        );
        request.setAttribute("chartDataJson", chartDataJson);

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/statistics.jsp");
        dispatch.forward(request, response);
    }
}