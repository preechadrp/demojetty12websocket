package demojetty12websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.ServletContext;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;

public class BroadcastSocket extends jakarta.websocket.Endpoint {

	private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.addMessageHandler(
				String.class,
				(jakarta.websocket.MessageHandler.Whole<String>) message -> {
					System.out.println("Received: " + message);
					try {
						session.getBasicRemote().sendText("ACK: " + message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

		sessions.add(session);
		System.out.println("WebSocket session opened: " + session.getId());
	}

	@Override
	public void onClose(Session session, jakarta.websocket.CloseReason closeReason) {
		sessions.remove(session);
		System.out.println("WebSocket session closed: " + session.getId());
	}

	@Override
	public void onError(Session session, Throwable throwable) {
		System.err.println("WebSocket Error: " + throwable.getMessage());
	}

	// เมธอดสำหรับ Broadcast
	public static void broadcast(String message) {
		synchronized (sessions) {
			for (Session session : sessions) {
				if (session.isOpen()) {
					try {
						session.getAsyncRemote().sendText(message);
					} catch (Exception e) {
						System.err.println("Error broadcasting: " + e.getMessage());
					}
				}
			}
		}
	}

	public static void configure(ServletContext context) {

		ServerContainer container = (ServerContainer) context.getAttribute(
				"jakarta.websocket.server.ServerContainer");

		if (container == null) {
			throw new IllegalStateException("WebSocket ServerContainer not initialized");
		}

		ServerEndpointConfig config = ServerEndpointConfig.Builder
				.create(BroadcastSocket.class, "/websocket/broadcast")
				.build();

		try {
			container.addEndpoint(config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}