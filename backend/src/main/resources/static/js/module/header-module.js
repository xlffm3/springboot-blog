export function renderLoginSection() {
  const token = localStorage.getItem('token');
  if (token === null) {
    const $loginSection = document.getElementById('login');
    const githubLoginHtml = document.querySelector('#template-login-github')
        .innerHTML;
    $loginSection.insertAdjacentHTML('beforeend', githubLoginHtml);
    const $loginBox = document.getElementById('login-github-box');
    $loginBox.addEventListener('click', startLogin);
    return;
  }
  renderUserInformationSection();
}

const startLogin = async () => {
  await axios.get('/api/authorization/github')
  .then(response => window.location.replace(response.data.url))
  .catch(error => alert(error));
}

function renderUserInformationSection() {
  const $loginSection = document.getElementById('login');
  const userInformationHtml = document.querySelector(
      '#template-user-information')
  .innerHTML.replace('{userName}', localStorage.getItem('userName'));
  $loginSection.insertAdjacentHTML('beforeend', userInformationHtml);

  const logoutAndWithdrawHtml = document.querySelector('#template-logout-withdraw')
      .innerHTML;
  $loginSection.insertAdjacentHTML('beforeend', logoutAndWithdrawHtml);
  const $logoutBox = document.getElementById('logout-box');
  $logoutBox.addEventListener('click', e => {
    localStorage.clear();
    location.reload();
  });
  const $withdrawBox = document.getElementById('withdraw-box');
  $withdrawBox.addEventListener('click', e => {
    alert('회원탈퇴를 하더라도 게시물 및 댓글은 삭제되지 않습니다.');
    requestToWithdraw();
  });
}

async function requestToWithdraw() {
  const token = localStorage.getItem('token');
  const url = '/api/users/withdraw';
  await axios.delete(url, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  }).then(response => {
    localStorage.clear();
    location.reload();
  }).catch(error => alert(error));
}

export function addLogoClickEvent() {
  document.getElementById('logo').addEventListener('click', ev => {
    sessionStorage.removeItem('page');
    window.location.replace('/');
  });
}
