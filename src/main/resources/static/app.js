const stompClient = new StompJs.Client({
  brokerURL: "ws://localhost:8080/ws",
});

stompClient.onConnect = (frame) => {
  setConnected(true);
  console.log("Connected: " + frame);
  // Custom headers to send along with the subscription
    const headers = {
      'Authorization': 'custom-value'
    };
  stompClient.subscribe("/channel/1234/subscribe", (greeting) => {
    showGreeting(JSON.parse(greeting.body).content);
  }, headers);
};

stompClient.onWebSocketError = (error) => {
  console.error("Error with websocket", error);
};

stompClient.onStompError = (frame) => {
  console.error("Broker reported error: " + frame.headers["message"]);
  console.error("Additional details: " + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  } else {
    $("#conversation").hide();
  }
  $("#greetings").html("");
}

function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  stompClient.publish({
    destination: "/app/channel/1234/chat",
    body: JSON.stringify({
        authToken: $("#token").val(),
    }),
    callback: (message) => {
      console.log(message);
    },
  });
}

function showGreeting(message) {
  $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
  $("form").on("submit", (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#send").click(() => sendName());
});
