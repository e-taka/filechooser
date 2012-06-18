package test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class HttpServer {
	private final Server _server;

	private HttpServer(final Server server) {
		_server = server;
	}

	public static HttpServer start() throws Exception {
		ServletHolder holder = new ServletHolder(HttpServletDispatcher.class);
		holder.setInitParameter(
				"javax.ws.rs.Application", "org.example.AjaxApplication");

		ServletContextHandler handler = new ServletContextHandler();
		handler.setContextPath("/filechooser-example");
		handler.setResourceBase("src/main/webapp");
		handler.setClassLoader(
				Thread.currentThread().getContextClassLoader());
		handler.addServlet(holder, "/ajax/*");
		handler.addServlet(DefaultServlet.class, "/*");

		Server server = new Server(8080);
		server.setHandler(handler);
		server.start();
		while (server.isStarting()) {
			Thread.sleep(1);
		}

		return new HttpServer(server);
	}

	public void stop() throws Exception {
		_server.stop();
		while (_server.isStopping()) {
			Thread.sleep(1);
		}
	}

	public static void main(final String[] args) throws Exception {
		HttpServer s = HttpServer.start();
		try {
			System.in.read();
		} finally {
			s.stop();
		}
	}
}
