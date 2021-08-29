import {renderLoginSection, addLogoClickEvent} from './module/header-module.js';
import {parseDate} from "./module/string-parser.js";
import {renderPageNavigation} from "./module/navigator-module.js";
import {activateButtonsSection} from "./module/button-module.js";
import {isLogin} from "./module/login-module.js";

const DEFAULT_SIZE_PER_PAGE = 10;
const DEFAULT_PAGE_BLOCK_COUNTS = 10;

function validateState() {
  const postId = sessionStorage.getItem('post-id');
  if (postId === null) {
    alert('유효하지 않은 접근입니다.');
    window.location.replace('/');
  }
}

async function renderPostSection() {
  const postId = sessionStorage.getItem('post-id');
  await axios.get('/api/posts/' + postId)
  .then(response => {
    const dto = response.data;
    document.getElementById('post-title').innerText = dto.title;
    document.getElementById('content').innerText = dto.content;
    document.getElementById('author-name').innerText += dto.author;
    document.getElementById('created-date').innerText +=
        parseDate(dto.createdDate);
    document.getElementById('modified-date').innerText +=
        parseDate(dto.modifiedDate);
    document.getElementById('view-counts')
        .innerText += dto.viewCounts;

    const $content = document.getElementById('content');
    const imageHtml = document.querySelector('#template-image')
        .innerHTML;
    Array.from(dto.imageUrls).forEach(url => {
      $content.insertAdjacentHTML('afterend',
          imageHtml.replace('{url}', url));
    })
    activateButtonsSection();
  });
}

async function renderCommentSection() {
  const postId = sessionStorage.getItem('post-id');
  let page = sessionStorage.getItem('page-comment');
  if (page === null) {
    page = '0';
    sessionStorage.setItem('page-comment', '0');
  }

  await axios.get('/api/posts/' + postId + '/comments', {
    params: {
      page: page,
      size: DEFAULT_SIZE_PER_PAGE,
      pageBlockCounts: DEFAULT_PAGE_BLOCK_COUNTS
    }
  }).then(response => {
    renderCommentRow(response);
    renderPageNavigation(response, 'page-comment', reRenderCommentSection);
  }).catch(error => alert(error));
}

function reRenderCommentSection() {
  Array.from(document.querySelectorAll('.mt-4'))
  .forEach(row => row.remove());
  Array.from(document.getElementsByClassName('page-link'))
  .forEach(button => button.remove());
  renderCommentSection();
}

function renderCommentRow(response) {
  const $table = document.getElementById('comment-board');

  response.data.commentResponses.forEach(comment => {
    const commentHtml = document.querySelector('#template-comment-row-template')
    .innerHTML.replaceAll('{comment-id}', comment.id)
    .replaceAll('{content}', comment.content)
    .replaceAll('{author}', comment.author)
    .replace('{created}', parseDate(comment.createdDate));
    $table.insertAdjacentHTML('beforeend', commentHtml);

    const $comment = document.getElementById(comment.id);
    const margin = (Number(comment.depth) - 1) * 20
    $comment.style.marginLeft = margin + "px";

    if (isLoginUserComment(comment)) {
      document.getElementById('comment-edit-btn-' + comment.id)
          .style.visibility = 'visible';
      document.getElementById('comment-delete-btn-' + comment.id)
          .style.visibility = 'visible';
    }
  });
  if (isLogin()) {
    const replyButtons =
        document.getElementsByClassName('comment-reply-btn-hidden');
    Array.from(replyButtons).forEach(btn => btn.style.visibility = "visible");
    activateReplyRequest();
    activateEditRequest();
    activateDeleteRequest();
  }
}

function isLoginUserComment(comment) {
  return isLogin() && comment.author === localStorage.getItem('userName');
}

function activateReplyRequest() {
  const replyButtons = document.querySelectorAll('.comment-reply-btn-submit');
  Array.from(replyButtons).forEach(replyButton => {
    replyButton.addEventListener('click', e => {
      const form = replyButton.closest('form');
      requestToReplyComment(form);
    });
  });
}

async function requestToReplyComment(form) {
  const parentCommentId = form.querySelector('#comment-reply-id').value;
  const commentContent = form.querySelector('#comment-reply-content').value;
  const postId = sessionStorage.getItem('post-id');
  const token = localStorage.getItem('token');
  const url = '/api/posts/' + postId + '/comments/' + parentCommentId
      + '/reply';
  const json = JSON.stringify({content: commentContent});

  await axios.post(url, json, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    }
  }).then(response => reRenderCommentSection())
  .catch(error => alert(error));
}

function activateEditRequest() {
  const editButtons = document.querySelectorAll('.comment-edit-btn-submit');
  Array.from(editButtons).forEach(editButton => {
    editButton.addEventListener('click', e => {
      const form = editButton.closest('form');
      requestToEditComment(form);
    });
  });
}

async function requestToEditComment(form) {
  const commentId = form.querySelector('#comment-edit-id').value;
  const commentContent = form.querySelector('#comment-edit-content').value;
  const token = localStorage.getItem('token');
  const url = '/api/comments/' + commentId;
  const json = JSON.stringify({content: commentContent});

  await axios.put(url, json, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    }
  }).then(response => reRenderCommentSection())
  .catch(error => alert(error));
}

function activateDeleteRequest() {
  const deleteButtons = document.querySelectorAll('.comment-delete-btn');
  Array.from(deleteButtons).forEach(deleteButton => {
    deleteButton.addEventListener('click', e => {
      const comment = deleteButton.closest('li');
      requestToDeleteComment(comment);
    });
  });
}

async function requestToDeleteComment(comment) {
  const token = localStorage.getItem('token');
  const url = '/api/comments/' + comment.id;
  await axios.delete(url, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  }).then(response => {
    alert('댓글이 삭제되었습니다.');
    reRenderCommentSection();
  }).catch(error => alert(error));
}

function activateCommentWriteSection() {
  if (!isLogin()) {
    return;
  }
  const commentFormHtml =
      document.querySelector('#template-comment-form').innerHTML;
  const $commentForm = document.getElementById('comment-form');
  $commentForm.insertAdjacentHTML('beforeend', commentFormHtml);
  const $submitButton = document.getElementById('submit-button');
  $submitButton.addEventListener('click', e => {
    requestToWriteComment(e);
  });
}

async function requestToWriteComment() {
  const comment = document.getElementById('comment').value;
  const postId = sessionStorage.getItem('post-id');
  const token = localStorage.getItem('token');
  const json = JSON.stringify({content: comment});

  await axios.post('/api/posts/' + postId + '/comments', json, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    }
  }).then(response => {
    document.getElementById('comment').value = '';
    reRenderCommentSection();
  })
  .catch(error => alert(error));
}

validateState();
renderLoginSection();
addLogoClickEvent();
renderPostSection();
renderCommentSection();
activateCommentWriteSection();
