const socket = io('http://localhost:8088');

const messageContainer = document.getElementById('message-container');
const messageInput = document.getElementById('message-input');
const sendButton = document.getElementById('send-button');

socket.on('chat-message', data => {
    appendMessage(`${data.name}: ${data.message}`);
});

sendButton.addEventListener('click', e => {
    e.preventDefault();
    const message = messageInput.value;
    const name = prompt('What is your name?');
    socket.emit('send-chat-message', { name, message });
    messageInput.value = '';
});

function appendMessage(message) {
    const messageElement = document.createElement('div');
    messageElement.innerText = message;
    messageContainer.append(messageElement);
}
