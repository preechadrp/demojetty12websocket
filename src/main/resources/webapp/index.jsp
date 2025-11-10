<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Jetty 12 WebSocket Broadcast Example</title>
</head>
<body>
	<h1>Server Broadcast Messages<%out.print("... my jsp");%></h1>
	<div id="messages"></div>

	<script type="text/javascript">
        // กำหนด URL ของ WebSocket Endpoint
        const wsUri = "ws://" + location.host + "/websocket/broadcast"; 
        let websocket;

        function init() {
            websocket = new WebSocket(wsUri);
            websocket.onopen = (evt) => {
                console.log("CONNECTED");
                displayMessage("Status: **Connected** to server.");
            };
            websocket.onmessage = (evt) => {
                const message = evt.data;
                console.log("RECEIVED: " + message);
                displayMessage("Received: " + message);
            };
            websocket.onclose = (evt) => {
                console.log("DISCONNECTED");
                displayMessage("Status: **Disconnected** from server.");
            };
            websocket.onerror = (evt) => {
                console.error("ERROR", evt);
                displayMessage("Status: **ERROR** - Check console for details.");
            };
        }

        function displayMessage(text) {
            const messagesDiv = document.getElementById("messages");
            const p = document.createElement("p");
            p.innerHTML = text;
            messagesDiv.appendChild(p);
            messagesDiv.scrollTop = messagesDiv.scrollHeight; 
        }

        window.addEventListener("load", init, false);
    </script>
</body>
</html>