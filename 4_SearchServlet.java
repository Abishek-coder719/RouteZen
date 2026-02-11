package com.routezen.servlet;

import com.routezen.model.Bus;
import com.routezen.model.SearchResult;
import com.routezen.service.BusScheduleService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet handling bus search requests
 * Processes user input and returns JSON results
 * 
 * @author Route Zen Team
 * @version 1.0
 */
@WebServlet("/api/search")
public class SearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BusScheduleService busService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        busService = new BusScheduleService();
        gson = new Gson();
        System.out.println("✓ SearchServlet initialized");
    }

    /**
     * Handles POST requests for bus search
     */
    @Override
    protected void doPost(HttpServletRequest request, 
                         HttpServletResponse response) 
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Extract parameters
            String fromStop = request.getParameter("fromStop");
            String toStop = request.getParameter("toStop");
            String boardingTime = request.getParameter("boardingTime");

            // Validate parameters
            if (fromStop == null || toStop == null || boardingTime == null ||
                fromStop.trim().isEmpty() || toStop.trim().isEmpty()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.write("{\"error\": \"Missing or invalid parameters\"}");
                out.flush();
                return;
            }

            // Check if same stop
            if (fromStop.equals(toStop)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.write("{\"error\": \"Origin and destination cannot be same\"}");
                out.flush();
                return;
            }

            // Search for buses
            List<SearchResult> results = busService.searchBuses(
                fromStop, toStop, boardingTime
            );

            // Convert to JSON and send response
            String jsonResponse = gson.toJson(results);

            PrintWriter out = response.getWriter();
            out.write(jsonResponse);
            out.flush();

            System.out.println("✓ Returned " + results.size() + " bus results");

        } catch (Exception e) {
            System.err.println("✗ Error processing search request: " + e.getMessage());
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("{\"error\": \"Internal server error\"}");
            out.flush();
        }
    }

    /**
     * Handles GET requests (for testing)
     */
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.getWriter().write(
            "{\"message\": \"Route Zen API - Use POST method to search buses\"}"
        );
    }
}
