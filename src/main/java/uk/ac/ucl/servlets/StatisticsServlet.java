package uk.ac.ucl.servlets;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.util.ChartDataBuilder;

@WebServlet("/statistics")
public class StatisticsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        Model model = ModelFactory.getModel();
        request.setAttribute("activePage", "statistics");

        // Key numbers
        request.setAttribute("totalPatients",       model.getNumPatients());
        request.setAttribute("oldestAliveAge",      model.getOldestAliveAge());
        request.setAttribute("youngestAliveAge",    model.getYoungestAliveAge());
        request.setAttribute("averageAliveAge",      String.format("%.1f", model.getAverageAliveAge()));
        request.setAttribute("averageAgeAtDeath",   String.format("%.1f", model.getAverageAgeAtDeath()));
        request.setAttribute("livingCount",         model.getLivingCount());
        request.setAttribute("distinctCityCount",   model.getDistinctValuesInCol("CITY").size());
        String[] topCity = model.getMostCommonCity();
        request.setAttribute("mostCommonCity",      topCity[0]);
        request.setAttribute("mostCommonCityCount", topCity[1]);
        request.setAttribute("mostCommonEthnicity", model.getMostCommonEthnicity());
        request.setAttribute("mostCommonRace",      model.getMostCommonRace());
        request.setAttribute("ethnicityTopN",       Model.ETHNICITY_CHART_TOP_N);

        // Chart data
        String chartDataJson = ChartDataBuilder.buildChartDataJson(
            ChartDataBuilder.mapToChartArrays(model.getGenderCounts()),
            ChartDataBuilder.mapToChartArrays(model.getMaritalCounts()),
            ChartDataBuilder.mapToChartArrays(model.getEthnicityCounts(Model.ETHNICITY_CHART_TOP_N)),
            ChartDataBuilder.mapToChartArrays(model.getLivingDeceasedCounts()),
            ChartDataBuilder.mapToChartArrays(model.getAliveAgeHistogram())
        );
        request.setAttribute("chartDataJson", chartDataJson);

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/statistics.jsp");
        dispatch.forward(request, response);
    }
}