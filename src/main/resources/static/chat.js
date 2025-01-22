window.stompClient = null;
window.accessToken = null;
window.USER = null
window.KEY = uuidv4();
window.ONLINE_USERS = [];
window.ON_GROUP = null;

const msgerForm = get(".msger-inputarea");
const msgerInput = get(".msger-input");
const msgerChat = get(".msger-chat");

const BOT_MSGS = [
  "Hi, how are you?",
  "Ohh... I can't understand what you trying to say. Sorry!",
  "I like to play games... But I don't know how to play!",
  "Sorry if my answers are not relevant. :))",
  "I feel sleepy! :("
];

// Icons made by Freepik from www.flaticon.com
const BOT_IMG = "https://image.flaticon.com/icons/svg/327/327779.svg";
const PERSON_IMG = "https://image.flaticon.com/icons/svg/145/145867.svg";
const BOT_NAME = "BOT";
const PERSON_NAME = "Sajad";

$(document).ready(function () {
    window.accessToken = localStorage.getItem('accessToken');
    window.stompClient = new StompJs.Client({
      brokerURL: "ws://localhost:8080/ws",
    });
    stompClient.activate();

    stompClient.onConnect = (frame) => {
        console.log("Connected: " + frame);
        stompClient.subscribe("/channel/auth/" + KEY, (data) => {
            const result = JSON.parse(data.body);
            console.log(result);
            USER = result.data;
            sendConnectMessage(result);
        });
        sendMessage("/app/channel/auth/" + KEY, {
            authToken: accessToken
        });
    };

    stompClient.onWebSocketError = (error) => {
      console.error("Error with websocket", error);
    };

    stompClient.onStompError = (frame) => {
      console.error("Broker reported error: " + frame.headers["message"]);
      console.error("Additional details: " + frame.body);
    };

    msgerForm.addEventListener("submit", event => {
      event.preventDefault();

      const msgText = msgerInput.value;
      if (!msgText) return;

      sendChatMessage(ON_GROUP, msgText);

      // appendMessage(PERSON_NAME, PERSON_IMG, "right", msgText);
      // msgerInput.value = "";

      // botResponse();
    });

    $(document).on('click', '.contact', function() {
      var key = $(this).data('key');
      const contact = ONLINE_USERS.find(user => user.key === key);
      ON_GROUP = contact;
      if (contact) {
        $('#chat_to').text(contact.user.username);
        console.log("Contact found:", contact);
        $('.msger-chat').empty();
        sendChatConnectMessage(contact);
      } else {
        console.log("Contact not found");
      }
    });
});

function sendMessage(topic, data) {
  stompClient.publish({
    destination: topic,
    body: JSON.stringify(data),
    callback: (message) => {
      console.log(message);
    },
  });
}

function sendConnectMessage(result) {
  stompClient.subscribe("/channel/online", (res) => {
    const data = JSON.parse(res.body).data;
    console.log(data);
    ONLINE_USERS = data;
    $('#contacts').empty();
    for (let i = 0; i < data.length; i++) {
      const contact = data[i];
      const htmlContact = `
        <div class="contact d-flex p-2" data-key="${contact.key}">
          <div
                  class="msg-img"
                  style="background-image: url(${contact.user.avatar})"
          ></div>
          <div class="d-flex flex-column">
              <div>${contact.user.username}</div>
              <div class="last-msg">Hello</div>
          </div>
        </div>
      `;
      $('#contacts').append(htmlContact);
    }
  });
  sendMessage("/app/channel/connect", {
      'userId': result.data.id,
      'channelId': result.data.key,
  });
}

function sendChatConnectMessage(contact) {
  stompClient.subscribe("/channel/" + contact.key + "/chat/connect", (res) => {
    const data = JSON.parse(res.body).data;
    console.log('Chat connected: ', data);
    $('.msger-chat').empty();
    for (let i = 0; i < data.length; i++) {
      const msg = data[i];
      appendMessage(msg.user.username, msg.user.avatar, msg.user.id === USER.id ? 'right' : "left", msg.content, msg.createdAt);
    }
    stompClient.subscribe("/channel/" + contact.key + "/chat", (res) => {
      const data = JSON.parse(res.body).data;
      const is_sender = USER.id === data.user.id;
      appendMessage(USER.username, USER.avatar, is_sender ? 'right' : "left", data.content, data.createdAt);
    });
  });
  sendMessage("/app/channel/" + contact.key + "/chat/connect", {});
}

function sendChatMessage(contact, text) {
  sendMessage("/app/channel/" + contact.key + "/chat", {
    'content': text,
    'type': 'TEXT',
  });
}

function appendMessage(name, img, side, text, date) {
  //   Simple solution for small apps
  const msgHTML = `
    <div class="msg ${side}-msg">
      <div class="msg-img" style="background-image: url(${img})"></div>

      <div class="msg-bubble">
        <div class="msg-info">
          <div class="msg-info-name">${name}</div>
          <div class="msg-info-time">${formatDate(new Date(date))}</div>
        </div>

        <div class="msg-text">${text}</div>
      </div>
    </div>
  `;

  msgerChat.insertAdjacentHTML("beforeend", msgHTML);
  msgerChat.scrollTop += 500;
}

function botResponse() {
  const r = random(0, BOT_MSGS.length - 1);
  const msgText = BOT_MSGS[r];
  const delay = msgText.split(" ").length * 100;

  setTimeout(() => {
    appendMessage(BOT_NAME, BOT_IMG, "left", msgText);
  }, delay);
}

// Utils
function get(selector, root = document) {
  return root.querySelector(selector);
}

function formatDate(date) {
  const h = "0" + date.getHours();
  const m = "0" + date.getMinutes();

  return `${h.slice(-2)}:${m.slice(-2)}`;
}

function random(min, max) {
  return Math.floor(Math.random() * (max - min) + min);
}

function uuidv4() {
  return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, c =>
    (+c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> +c / 4).toString(16)
  );
}

