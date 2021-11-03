import {isLogin} from "./login-module.js";

export function activateButtonsSection() {
  if (!isLogin()) {
    return;
  }
  const $postButtonSection = document.getElementById('button-section');
  const postWriteButtonHtml =
      document.querySelector('#template-post-write-button').innerHTML;
  $postButtonSection.insertAdjacentHTML('beforeend', postWriteButtonHtml);

  const authorName = document.getElementById('author-name');
  if (authorName === null) {
    return;
  }

  const userName = localStorage.getItem('userName');
  const parsedName = authorName.innerText.split(':')[1].trim();
  if (userName !== parsedName) {
    return;
  }

  const postEditButtonHtml =
      document.querySelector('#template-post-edit-button').innerHTML;
  const postDeleteButtonHtml =
      document.querySelector('#template-post-delete-button').innerHTML;
  $postButtonSection.insertAdjacentHTML('beforeend', postEditButtonHtml);
  $postButtonSection.insertAdjacentHTML('beforeend', postDeleteButtonHtml);

  document.getElementById('edit-submit-btn')
  .addEventListener('click', e => {
    const title = document.getElementById('post-title').innerText;
    const content = document.getElementById('content').innerText;
    const author = document.getElementById('author-name').innerText;
    sessionStorage.setItem('edit-title', title);
    sessionStorage.setItem('edit-content', content);
    sessionStorage.setItem('edit-author', author);
    window.location.replace('/page/post/edit');
  });
  document.getElementById('delete-submit-btn')
  .addEventListener('click', e => requestToDeletePost());
}

async function requestToDeletePost() {
  const postId = sessionStorage.getItem('post-id');
  const url = '/api/posts/' + postId;
  const token = localStorage.getItem('token');

  await axios.delete(url, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  }).then(response => {
    alert('게시물이 삭제되었습니다.');
    window.location.replace('/');
  })
  .catch(error => alert(error));
}
