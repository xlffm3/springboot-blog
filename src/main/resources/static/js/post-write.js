import {addLogoClickEvent, renderLoginSection} from "./module/header-module.js";

function validateState() {
  const token = localStorage.getItem('token');
  if (token === null) {
    alert('유효하지 않은 접근입니다.');
    window.location.replace('/');
  }
}

function addPostWriteButtonEvent() {
  const $submitButton = document.getElementById('submit-button');
  $submitButton.addEventListener('click', e => {
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    requestToWritePost(title, content);
  });
}

async function requestToWritePost(title, content) {
  const json = JSON.stringify({title: title, content: content});
  const token = localStorage.getItem('token');
  await axios.post('/api/posts', json, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    }
  }).then(response => moveToPostPage(response))
  .catch(error => alert(error));
}

function moveToPostPage(response) {
  const location = response.headers.location;
  const lastIndex = location.lastIndexOf('/');
  const postId = location.substring(lastIndex + 1);
  sessionStorage.setItem('post-id', postId);
  window.location.replace('/page/post')
}

validateState();
renderLoginSection();
addLogoClickEvent();
addPostWriteButtonEvent();
