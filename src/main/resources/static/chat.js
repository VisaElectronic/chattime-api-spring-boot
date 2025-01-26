window.API_BASE_DOMAIN = "172.20.10.2:8080";
window.stompClient = null;
window.accessToken = null;
window.USER = null;
window.KEY = uuidv4();
window.ONLINE_USERS = [];
window.ON_GROUP = null;
window.GROUP_CONNECT = null;
window.CHAT_CONNECT = null;

const msgerForm = get(".msger-inputarea");
const msgerInput = get(".msger-input");
const msgerChat = get(".msger-chat");

const BOT_MSGS = [
  "Hi, how are you?",
  "Ohh... I can't understand what you trying to say. Sorry!",
  "I like to play games... But I don't know how to play!",
  "Sorry if my answers are not relevant. :))",
  "I feel sleepy! :(",
];

// Icons made by Freepik from www.flaticon.com
const BOT_IMG = "https://image.flaticon.com/icons/svg/327/327779.svg";
const PERSON_IMG = "https://image.flaticon.com/icons/svg/145/145867.svg";
const BOT_NAME = "BOT";
const PERSON_NAME = "Sajad";

$(document).ready(function () {
  window.accessToken = localStorage.getItem("accessToken");
  window.stompClient = new StompJs.Client({
    brokerURL: "ws://" + API_BASE_DOMAIN + "/ws",
  });
  stompClient.activate();

  stompClient.onConnect = (frame) => {
    stompClient.subscribe("/channel/auth/" + KEY, (data) => {
      const result = JSON.parse(data.body);
      console.log("CONNECTED");
      USER = result.data;
      $("#current_user").css("background-image", "url(" + USER.avatar + ")");
      sendConnectMessage(result);
    });
    sendMessage("/app/channel/auth/" + KEY, {
      authToken: accessToken,
    });
  };

  stompClient.onWebSocketError = (error) => {
    console.error("Error with websocket", error);
  };

  stompClient.onStompError = (frame) => {
    console.error("Broker reported error: " + frame.headers["message"]);
    console.error("Additional details: " + frame.body);
  };

  msgerForm.addEventListener("submit", (event) => {
    event.preventDefault();

    const msgText = msgerInput.value;
    if (!msgText) return;

    sendChatMessage(ON_GROUP, msgText);
  });

  $(document).on("click", ".contact", function () {
    var key = $(this).data("key");
    const contact = ONLINE_USERS.find((group) => group.key === key);
    ON_GROUP = contact;
    if (contact) {
      $("#chat_to").text(contact.channels[0].user.username);
      $("#chat_to_avatar").removeClass("d-none");
      $("#chat_to_avatar").css(
        "background-image",
        "url(" + contact.channels[0].user.avatar + ")"
      );
      const connected = ONLINE_USERS.find(
        (group) => group.key === contact.key && group.connected
      );
      // if(connected) {
      //   sendChatConnectMessage(contact, true);
      //   return;
      // }
      $(".msger-chat").empty();
      sendChatConnectMessage(contact);
    } else {
      console.log("Contact not found");
    }
  });

  $(".js-data-example-ajax").select2({
    width: "100%",
    dropdownParent: $(".modal"),
    ajax: {
      url: "http://" + API_BASE_DOMAIN + "/api/user",
      dataType: "json",
      headers: {
        Authorization: "Bearer " + accessToken,
      },
      data: function (params) {
        var query = {
          search: params.term,
        };
        return query;
      },
      processResults: function (data) {
        return {
          results: data.data.map((user) => {
            return {
              id: user.id,
              text: user.email,
              avatar: user.avatar, // Add avatar to the result
            };
          }),
        };
      },
    },
    templateResult: function (user) {
      if (user.loading) {
        return user.text;
      }
      var $container = $(
        `<div class="select2-result-user clearfix d-flex align-items-center gap-2">
          <div class="select2-result-user__avatar"><img src="${user.avatar}" /></div>
          <div class="select2-result-user__meta">
          <div class="select2-result-user__email">${user.text}</div>
          </div>
        </div>`
      );
      return $container;
    },
    templateSelection: function (user) {
      return user.text || user.email;
    },
  });

  $("#add_user").click(function () {
    const userId = $('[name="added_user"]').val();
    if (userId) {
      $.ajax({
        url: "http://" + API_BASE_DOMAIN + "/api/contacts",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ userId: userId }),
        headers: {
          Authorization: "Bearer " + accessToken,
        },
        success: function (response) {
          location.reload();
        },
        error: function (error) {
          console.error("Error adding user to channel:", error);
        },
      });
    }
  });

  $('#logout').click(function() {
    localStorage.removeItem("accessToken");
    window.location.href = "index.html";
  });
});

function sendMessage(topic, data) {
  stompClient.publish({
    destination: topic,
    body: JSON.stringify(data),
    callback: (message) => {
      console.log("message:", message);
    },
  });
}

function sendConnectMessage(result) {
  stompClient.subscribe("/channel/" + result.data.key + "/online", (res) => {
    const data = JSON.parse(res.body).data;
    // only add new connection to array , check if connection already exist
    data.forEach((newUser) => {
      const exists = ONLINE_USERS.some((user) => user.key === newUser.key);
      if (!exists) {
        ONLINE_USERS.push(newUser);
      }
    });
    $("#contacts").empty();
    for (let i = 0; i < data.length; i++) {
      const group = data[i];
      const contact = group.channels[0];
      const htmlContact = `
        <div class="contact d-flex p-2" data-key="${group.key}">
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
      $("#contacts").append(htmlContact);
    }
  });
  sendMessage("/app/channel/" + result.data.key + "/connect", {
    userId: result.data.id,
    channelId: result.data.key,
  });
}

function sendChatConnectMessage(contact, isSubscribe = false) {
  // if(isSubscribe) {
  //   sendMessage("/app/channel/" + contact.key + "/chat/connect", {
  //     isGroup: contact.channels[0].user ? false : true,
  //   });
  //   return;
  // }
  // stompClient.unsubscribe("/channel/" + contact.key + "/chat/connect");
  if (GROUP_CONNECT) GROUP_CONNECT.unsubscribe();
  GROUP_CONNECT = stompClient.subscribe(
    "/channel/" + contact.key + "/chat/connect",
    (res) => {
      const data = JSON.parse(res.body).data;
      if (data.length > 0 && data[0].group.key !== ON_GROUP.key) return;
      $(".msger-chat").empty();
      for (let i = 0; i < data.length; i++) {
        const msg = data[i];
        appendMessage(
          msg.user.username,
          msg.user.avatar,
          msg.user.id === USER.id ? "right" : "left",
          msg.content,
          msg.createdAt
        );
      }
      // // check if user is connected to chat then don't subscribe again
      // const connected = ONLINE_USERS.find(group => group.key === contact.key && group.connected);
      // if(connected) return;
      if (CHAT_CONNECT) CHAT_CONNECT.unsubscribe();
      CHAT_CONNECT = stompClient.subscribe(
        "/channel/" + contact.key + "/chat",
        (res) => {
          const data = JSON.parse(res.body).data;
          console.log("chat: ", data);
          const is_sender = USER.id === data.user.id;
          appendMessage(
            data.user.username,
            data.user.avatar,
            is_sender ? "right" : "left",
            data.content,
            data.createdAt
          );
        }
      );
      // set connected to true
      ONLINE_USERS.forEach((element) => {
        if (element.key == contact.key) element.connected = true;
      });
    }
  );
  sendMessage("/app/channel/" + contact.key + "/chat/connect", {
    isGroup: contact.channels[0].user ? false : true,
  });
}

function subscribeChatChannel() {}

function sendChatMessage(contact, text) {
  var channel = contact.channels[0];
  sendMessage("/app/channel/" + contact.key + "/chat", {
    content: text,
    type: "TEXT",
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
  return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, (c) =>
    (
      +c ^
      (crypto.getRandomValues(new Uint8Array(1))[0] & (15 >> (+c / 4)))
    ).toString(16)
  );
}
