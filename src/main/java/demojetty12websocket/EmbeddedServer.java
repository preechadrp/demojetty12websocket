package demojetty12websocket;

import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class EmbeddedServer {
	public static void main(String[] args) throws Exception {
		Server server = new Server();

		// กำหนด Connector
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.addConnector(connector);

		// **ใช้ ServletContextHandler สำหรับ Jetty 12**
		// นี่คือ Handler พื้นฐานที่รองรับ Servlet และ WebSocket
		org.eclipse.jetty.ee10.servlet.ServletContextHandler context = new org.eclipse.jetty.ee10.servlet.ServletContextHandler();
		context.setContextPath("/");
		//context.setInitParameter("dirAllowed", "false");    ไม่ต้องเพราะไม่ได้ใช้ DefaultServlet
		//context.setInitParameter("welcomeServlets", "true");  ไม่ต้องเพราะไม่ได้ใช้ DefaultServlet

		context.addServlet(new ServletHolder(new StaticResourceServlet()), "/*");
		
		// ตั้งค่า WebSocket แบบ programmatic       
		JakartaWebSocketServletContainerInitializer.configure(
				context,
				(servletContext, serverContainer) -> {
					BroadcastSocket.configure(servletContext);
				});

		server.setHandler(context);
		server.setStopTimeout(60000);
		server.setStopAtShutdown(true);

		// 3. เริ่มต้น Server และ Broadcast จำลอง
		server.start();

		// ตัวอย่างการ Broadcast จาก Server (เหมือนเดิม)
		new Thread(() -> {
			int count = 0;
			while (server.isRunning()) {
				try {
					Thread.sleep(2000);
					String message = "Server Broadcast #" + (++count) + " @ " + System.currentTimeMillis();
					BroadcastSocket.broadcast(message);
					System.out.println("SERVER ACTION: Broadcasted: " + message);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}).start();

		server.join();
	}
}