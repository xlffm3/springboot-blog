export function activatePostWriteButton() {
  const token = localStorage.getItem('token');
  if (token === null) {
    return;
  }
  const $postButtonSection = document.getElementById('button-section');
  const postWriteButtonHtml =
      document.querySelector('#template-post-write-button').innerHTML;
  $postButtonSection.insertAdjacentHTML('beforeend', postWriteButtonHtml);
}
