export function renderPageNavigation(response, redirectUrl, pageKey) {
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
      sessionStorage.setItem(pageKey, startPage - 2);
      window.location.replace(redirectUrl);
    });
  }

  for (let i = startPage; i <= endPage; i++) {
    const pageButtonHtml =
        document.querySelector('#template-page-button').innerHTML
        .replaceAll('{index}', i);
    $navigator.insertAdjacentHTML('beforeend', pageButtonHtml);
    const $pageButton = document.getElementById('page-button-' + i);
    $pageButton.addEventListener('click', e => {
      sessionStorage.setItem(pageKey, i - 1);
      window.location.replace(redirectUrl);
    });
  }

  if (next === true && endPage > 0) {
    const nextHtml = document.querySelector('#template-next-button')
        .innerHTML;
    $navigator.insertAdjacentHTML('beforeend', nextHtml);
    const $nextButton = document.getElementById('next');
    $nextButton.addEventListener('click', e => {
      sessionStorage.setItem(pageKey, endPage);
      window.location.replace(redirectUrl);
    });
  }

  const currentPage = Number(sessionStorage.getItem(pageKey)) + 1;
  const $activeButton = document.getElementById('page-button-' + currentPage);
  if ($activeButton !== null) {
    $activeButton.parentNode.className = 'page-item active';
  }
}
