package de.oglimmer.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/*")
public class GetServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String s = uri.substring(uri.indexOf(contextPath) + contextPath.length());
		if (s.isEmpty() || s.equals("/")) {
			resp.sendRedirect(contextPath + "/faces/index.xhtml");
		} else {
			resp.sendRedirect(contextPath + "/faces/display.xhtml?id=" + s.substring(1));
		}

	}

}
