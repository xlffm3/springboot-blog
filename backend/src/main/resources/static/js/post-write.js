import {addLogoClickEvent, renderLoginSection} from "./module/header-module.js";
import {isLogin} from "./module/login-module.js";

function validateState() {
  if (!isLogin()) {
    alert('유효하지 않은 접근입니다.');
    window.location.replace('/');
  }
}

function addPostWriteButtonEvent() {
  const $submitButton = document.getElementById('submit-button');
  $submitButton.addEventListener('click', e => {
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    const files = document.querySelector('input[type=file]').files
    requestToWritePost(title, content, files);
  });
}

async function requestToWritePost(title, content, files) {
  let formData = new FormData();
  const token = localStorage.getItem('token');
  formData.append('title', title);
  formData.append('content', content);
  Array.from(files).forEach(file => formData.append('files', file));
  await axios.post('/api/posts', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
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
