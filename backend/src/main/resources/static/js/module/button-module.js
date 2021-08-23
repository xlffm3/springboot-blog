export function activateButtonsSection() {
  const token = localStorage.getItem('token');
  if (token === null) {
    return;
  }
  const $postButtonSection = document.getElementById('button-section');
  const postWriteButtonHtml =
      document.querySelector('#template-post-write-button').innerHTML;
  $postButtonSection.insertAdjacentHTML('beforeend', postWriteButtonHtml);

  const userName = localStorage.getItem('userName');
  const authorName = document.getElementById('author-name');
  if (authorName === null) {
    return;
  }
  const parsedName = authorName.innerText.split(':')[1].trim();
  if (userName === parsedName) {
    const postEditButtonHtml =
        document.querySelector('#template-post-edit-button').innerHTML;
    $postButtonSection.insertAdjacentHTML('beforeend', postEditButtonHtml);
  }
}
