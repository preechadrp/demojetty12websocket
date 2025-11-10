package demojetty12websocket;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import org.eclipse.jetty.ee10.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.ee10.jsp.JettyJspServlet;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import jakarta.servlet.ServletContext;


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
        // แบบเพิ่มความสามารถด้าน jsp
        //org.eclipse.jetty.ee10.webapp.WebAppContext context = new org.eclipse.jetty.ee10.webapp.WebAppContext();
        context.setContextPath("/");
        
        // กำหนด Base Resource ให้ชี้ไปที่โฟลเดอร์สำหรับ JSP และไฟล์อื่นๆ
        // (เช่น 'src/main/webapp' ถ้าใช้ Maven/IDE มาตรฐาน)
        URL rscURL = EmbeddedServer.class.getResource("/webapp");
		context.setBaseResourceAsString(rscURL.toExternalForm());
		
		//===== jsp  ตัว context ต้องเป็น WebAppContext ======//
		// กำหนด Temp Directory สำหรับ JSP Compilation** 
		// JSP ต้องมีที่เก็บไฟล์ Java ที่ถูกคอมไพล์ (Scratch directory) 
		File tempDir = Files.createTempDirectory("jetty-jsp-scratch").toFile();
		tempDir.deleteOnExit(); // ลบเมื่อโปรแกรมปิด 
		context.setAttribute(ServletContext.TEMPDIR, tempDir);
		System.out.println("JSP Scratch Directory: " + tempDir.getAbsolutePath());
		context.addServletContainerInitializer(new JettyJasperInitializer());// เพิ่ม JSP Initializer** 
		context.addServlet(new JettyJspServlet(), "*.jsp");// เพิ่มตัวจัดการสำหรับไฟล์ *.jsp 
		//===== jsp ======//
        
        // **สำคัญ**: ตั้งค่า DefaultServlet สำหรับ JSP และ Static Files
        // ใช้ DefaultServlet จาก ee10
        context.addServlet(DefaultServlet.class, "/");
        
        // **สำคัญ**: ตั้งค่า ClassLoader ให้โหลด Javax/Jakarta Namespace (สำหรับ JSP)
        context.setClassLoader(Thread.currentThread().getContextClassLoader());

        // ตั้งค่า WebSocket
        org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer.configure(context, (servletContext, container) -> {
            // ลงทะเบียน Endpoint
            container.addEndpoint(BroadcastSocket.class);
        });

        server.setHandler(context);

        // 3. เริ่มต้น Server และ Broadcast จำลอง
        server.start();

        // ตัวอย่างการ Broadcast จาก Server (เหมือนเดิม)
        new Thread(() -> {
            int count = 0;
            while (server.isRunning()) {
                try {
                    Thread.sleep(5000); 
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