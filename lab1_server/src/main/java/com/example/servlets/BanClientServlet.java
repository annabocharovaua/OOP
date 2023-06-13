package com.example.servlets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;

import java.io.*;

@WebServlet(name = "banClientServlet", value = "/ban-client")
public class BanClientServlet extends DBServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);

        PrintWriter out = response.getWriter();

        if(requestJsonData == null){
            out.println("EMPTY REQUEST");
            logger.info("EMPTY REQUEST");
            return;
        }
        if(dbManager==null){
            out.println("NO DB CONNECTION");
            logger.info("NO DB CONNECTION");
            return;
        }

        try {
            int clientId = requestJsonData.getInt("clientId");
            dbManager.BanClient(clientId);
            out.println("true");
            logger.info("BAN CLIENT WITH ID " + clientId);
        } catch (JSONException e) {
            out.println("DOESN'T CONTAIN REQUIRED KEY");
            logger.info("DOESN'T CONTAIN REQUIRED KEY");
        }

        out.close();
    }
}
