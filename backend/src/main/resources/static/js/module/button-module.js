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
    const postDeleteButtonHtml =
        document.querySelector('#template-post-delete-button').innerHTML;
    $postButtonSection.insertAdjacentHTML('beforeend', postDeleteButtonHtml);

    document.getElementById('delete-submit-btn')
    .addEventListener('click', e => {
        requestToDeletePost();
    });
  }
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
