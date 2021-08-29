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
      pageBlockCounts: DEFAULT_PAGE_BLOCK_COUNTS
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
      window.location.replace('/page/post')
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

renderLoginSection();
addLogoClickEvent();
renderBoardSection();
activateButtonsSection();
