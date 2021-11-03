import {addLogoClickEvent, renderLoginSection} from "./module/header-module.js";
import {isLogin} from "./module/login-module.js";

function validateState() {
  const postId = sessionStorage.getItem('post-id');
  const author = sessionStorage.getItem('edit-author')
  .toString().split(' : ')[1];
  const loginUserName = localStorage.getItem('userName');

  if (!isLogin() || postId === null || author !== loginUserName) {
    alert('유효하지 않은 접근입니다.');
    window.location.replace('/');
  }

  const title = sessionStorage.getItem('edit-title');
  const content = sessionStorage.getItem('edit-content');
  document.getElementById('title').innerText = title;
  document.getElementById('content').innerText = content;
}

function addPostEditButtonEvent() {
  const $submitButton = document.getElementById('submit-button');
  $submitButton.addEventListener('click', e => {
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    requestToEditPost(title, content);
  });
}

async function requestToEditPost(title, content) {
  const postId = sessionStorage.getItem('post-id');
  const token = localStorage.getItem('token');
  const json = JSON.stringify({title: title, content: content});
  await axios.put('/api/posts/' + postId, json, {
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
  sessionStorage.removeItem('edit-author');
  sessionStorage.removeItem('edit-title');
  sessionStorage.removeItem('edit-content');
  sessionStorage.setItem('post-id', postId);
  window.location.replace('/page/post/' + postId)
}

validateState();
renderLoginSection();
addLogoClickEvent();
addPostEditButtonEvent();
