package de.oglimmer.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/")
public class GetServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String s = uri.substring(uri.indexOf(contextPath) + contextPath.length());
		if (s.isEmpty() || s.equals("/")) {
			req.getRequestDispatcher("/faces/index.xhtml").forward(req, resp);
		} else {
			req.setAttribute("id", s.substring(1));
			req.getRequestDispatcher("/faces/display.xhtml").forward(req, resp);
		}

	}

}
