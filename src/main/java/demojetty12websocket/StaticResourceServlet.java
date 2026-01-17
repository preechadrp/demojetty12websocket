package demojetty12websocket;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaticResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String path = req.getPathInfo();
		if (path == null || "/".equals(path)) {
			path = "/index.html";
		}

		// ป้องกัน path traversal
		if (path.contains("..")) {
			resp.sendError(403);
			return;
		}

		String resourcePath = "/webapp" + path;

		try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
			if (is == null) {
				resp.sendError(404);
				return;
			}

			resp.setContentType(guessContentType(path));
			is.transferTo(resp.getOutputStream());
		}
	}

	private String guessContentType(String path) {
		if (path.endsWith(".html"))
			return "text/html; charset=UTF-8";
		if (path.endsWith(".css"))
			return "text/css";
		if (path.endsWith(".js"))
			return "application/javascript";
		if (path.endsWith(".png"))
			return "image/png";
		return "application/octet-stream";
	}
}
