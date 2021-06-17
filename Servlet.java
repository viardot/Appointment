import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;


public class Servlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
    String requestUrl = request.getRequestURI();
		
     //not implemented yet
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    String subject = request.getParameter("subject");
	String startDateTime = request.getParameter("startDateTime");
	String endDateTime = request.getParameter("endDateTime");
        
  }
}