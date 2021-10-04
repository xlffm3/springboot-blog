import {renderLoginSection, addLogoClickEvent} from './module/header-module.js';
import {parseDate} from "./module/string-parser.js";
import {renderPageNavigation} from "./module/navigator-module.js";
import {activateButtonsSection} from "./module/button-module.js";

const DEFAULT_SIZE_PER_PAGE = 10;
const DEFAULT_PAGE_BLOCK_COUNTS = 10;

async function renderBoardSection() {
  let page = sessionStorage.getItem('page-post');
  if (page === null) {
    page = '0';
    sessionStorage.setItem('page-post', '0');
  }

  await axios.get('/api/posts', {
    params: {
      page: page,
      size: DEFAULT_SIZE_PER_PAGE,
      pageBlockCounts: DEFAULT_PAGE_BLOCK_COUNTS,
      searchType: sessionStorage.getItem('searchType'),
      keyword: sessionStorage.getItem('keyword')
    }
  }).then(response => {
    renderPostRow(response);
    renderPageNavigation(response, 'page-post', reRenderBoardSection);
  }).catch(error => alert(error));
}

function renderPostRow(response) {
  const $table = document.getElementById('tbody');

  response.data.simplePostResponses.forEach(post => {
    const postRowHtml =
        document.querySelector('#template-post-row-template')
        .innerHTML.replace('{post-id}', post.id)
        .replace('{index}', post.id)
        .replace('{title}', post.title)
        .replace('{author}', post.author)
        .replace('{views}', post.viewCounts)
        .replace('{created}', parseDate(post.createdDate));
    $table.insertAdjacentHTML('beforeend', postRowHtml);

    const $post = document.getElementById(post.id);
    $post.addEventListener('click', e => {
      sessionStorage.setItem('post-id', post.id);
      window.location.replace('/page/post/' + post.id)
    });
  });
}

function reRenderBoardSection() {
  Array.from(document.getElementsByClassName('post-row'))
  .forEach(row => row.remove());
  Array.from(document.getElementsByTagName('li'))
  .forEach(button => button.remove());
  renderBoardSection();
}

function activateSearchButton() {
  const $searchButton = document.getElementById('search-btn');
  $searchButton.addEventListener('click', ev => {
    const selectBox = document.getElementById('select-box');
    const keyword = document.getElementById('input-box').value;
    const searchType = selectBox.options[selectBox.selectedIndex].value;
    if (keyword === null || searchType === null) {
      return;
    }
    sessionStorage.setItem('keyword', keyword);
    sessionStorage.setItem('searchType', searchType);
    sessionStorage.removeItem('page-post');
    reRenderBoardSection();
  });

  const $resetButton = document.getElementById('search-reset-btn');
  $resetButton.addEventListener('click', ev => {
    sessionStorage.removeItem('keyword');
    sessionStorage.removeItem('searchType');
    sessionStorage.removeItem('page-post');
    reRenderBoardSection();
  });
}

renderLoginSection();
addLogoClickEvent();
renderBoardSection();
activateButtonsSection();
activateSearchButton();
