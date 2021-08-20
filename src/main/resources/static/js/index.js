import {renderLoginSection, addLogoClickEvent} from './module/header-module.js';
import {parseDate} from "./module/string-parser.js";

const DEFAULT_SIZE_PER_PAGE = 10;
const DEFAULT_PAGE_BLOCK_COUNTS = 10;

async function renderBoardSection() {
  let page = sessionStorage.getItem('page');
  if (page === null) {
    page = '0';
    sessionStorage.setItem('page', '0');
  }

  await axios.get('/api/posts', {
    params: {
      page: page,
      size: DEFAULT_SIZE_PER_PAGE,
      pageBlockCounts: DEFAULT_PAGE_BLOCK_COUNTS
    }
  }).then(response => {
    renderPostRow(response);
    renderPageNavigation(response);
  }).catch(error => alert(error));
}

function renderPostRow(response) {
  const $table = document.getElementById('tbody');

  response.data.postResponse.forEach(post => {
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
      window.location.replace('/page/post')
    });
  });
}

function renderPageNavigation(response) {
  const $navigator = document.getElementById('page-navigator');
  const startPage = response.data.startPage;
  const endPage = response.data.endPage;
  const prev = response.data.prev;
  const next = response.data.next;

  if (prev === true) {
    const prevHtml = document.querySelector('#template-prev-button')
        .innerHTML;
    $navigator.insertAdjacentHTML('beforeend', prevHtml);
    const $prevButton = document.getElementById('prev');
    $prevButton.addEventListener('click', e => {
      sessionStorage.setItem('page', startPage - 2);
      window.location.replace('/');
    });
  }

  for (let i = startPage; i <= endPage; i++) {
    const pageButtonHtml =
        document.querySelector('#template-page-button').innerHTML
        .replaceAll('{index}', i);
    $navigator.insertAdjacentHTML('beforeend', pageButtonHtml);
    const $pageButton = document.getElementById('page-button-' + i);
    $pageButton.addEventListener('click', e => {
      sessionStorage.setItem('page', i - 1);
      window.location.replace('/');
    });
  }

  if (next === true && endPage > 0) {
    const nextHtml = document.querySelector('#template-next-button')
        .innerHTML;
    $navigator.insertAdjacentHTML('beforeend', nextHtml);
    const $nextButton = document.getElementById('next');
    $nextButton.addEventListener('click', e => {
      sessionStorage.setItem('page', endPage);
      window.location.replace('/');
    });
  }

  const currentPage = Number(sessionStorage.getItem('page')) + 1;
  const $activeButton = document.getElementById('page-button-' + currentPage);
  $activeButton.parentNode.className = 'page-item active';
}

renderLoginSection();
addLogoClickEvent();
renderBoardSection();

