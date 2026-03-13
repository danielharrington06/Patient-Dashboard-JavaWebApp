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
        request.setAttribute("genderCounts",     model.getGenderCounts());
        request.setAttribute("maritalCounts",    model.getMaritalCounts());
        request.setAttribute("ethnicityCounts",  model.getEthnicityCounts(5));
        request.setAttribute("raceCounts",       model.getRaceCounts());

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/stats.jsp");
        dispatch.forward(request, response);
    }
}