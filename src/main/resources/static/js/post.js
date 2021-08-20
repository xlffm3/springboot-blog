import {renderLoginSection, addLogoClickEvent} from './module/header-module.js';
import {parseDate} from "./module/string-parser.js";

function validateState() {
  const postId = sessionStorage.getItem('post-id');
  if (postId === null) {
    alert('유효하지 않은 접근입니다.');
    window.location.replace('/');
  }
  sessionStorage.setItem('redirect-url', '/page/post');
}

async function renderPostSection() {
  const postId = sessionStorage.getItem('post-id');
  await axios.get('/api/posts/' + postId)
  .then(response => {
    const dto = response.data;
    document.getElementById('post-title').innerText = dto.title;
    document.getElementById('content').innerText = response.data.content;

    document.getElementById('author-name').innerText += response.data.author;
    document.getElementById('created-date').innerText += parseDate(response.data.createdDate);
    document.getElementById('modified-date').innerText +=  parseDate(response.data.modifiedDate);
    document.getElementById('view-counts').innerText += response.data.viewCounts;
  });
}

validateState();
renderLoginSection();
addLogoClickEvent();
renderPostSection();
