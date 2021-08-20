import {renderLoginSection, addLogoClickEvent} from './module/header-module.js';
import {parseDate, appendReply} from "./module/string-parser.js";
import {renderPageNavigation} from "./module/navigator-module.js";
import {activatePostWriteButton} from "./module/button-module.js";

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
    document.getElementById('content').innerText = response.data.content;

    document.getElementById('author-name').innerText += response.data.author;
    document.getElementById('created-date').innerText +=
        parseDate(response.data.createdDate);
    document.getElementById('modified-date').innerText +=
        parseDate(response.data.modifiedDate);
    document.getElementById('view-counts')
        .innerText += response.data.viewCounts;
  });
}

async function renderCommentSection() {
  const postId = sessionStorage.getItem('post-id');
  let page = sessionStorage.getItem('page-comment');
  if (page === null) {
    page = '0';
    sessionStorage.setItem('page-comment', '0');
  }

  await axios.get('/api/comments/' + postId, {
    params: {
      page: page,
      size: DEFAULT_SIZE_PER_PAGE,
      pageBlockCounts: DEFAULT_PAGE_BLOCK_COUNTS
    }
  }).then(response => {
    renderCommentRow(response);
    renderPageNavigation(response, '/page/post', 'page-comment');
  }).catch(error => alert(error));
}

function renderCommentRow(response) {
  const $table = document.getElementById('tbody');

  response.data.commentResponses.forEach(comment => {
    const commentHtml = document.querySelector('#template-comment-row-template')
    .innerHTML.replace('{comment-id}', comment.id)
    .replace('{author}', comment.author)
    .replace('{content}', appendReply(comment.content, comment.depth))
    .replace('{created}', parseDate(comment.createdDate));
    $table.insertAdjacentHTML('beforeend', commentHtml);
  });
}

validateState();
renderLoginSection();
addLogoClickEvent();
renderPostSection();
renderCommentSection();
activatePostWriteButton();
