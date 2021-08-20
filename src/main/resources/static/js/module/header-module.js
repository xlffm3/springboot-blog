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

  const logoutHtml = document.querySelector('#template-logout')
      .innerHTML;
  $loginSection.insertAdjacentHTML('beforeend', logoutHtml);
  const $logoutBox = document.getElementById('logout-box');
  $logoutBox.addEventListener('click', e => {
    localStorage.clear();
    moveToRedirectUrl();
  });
}

export function addLogoClickEvent() {
  document.getElementById('logo').addEventListener('click', ev => {
    sessionStorage.removeItem('page');
    window.location.replace('/');
  });
}

export function moveToRedirectUrl() {
  const redirectUrl = sessionStorage.getItem('redirect-url');
  if (redirectUrl === null) {
    window.location.return('/');
  }
  sessionStorage.removeItem('redirect-url');
  window.location.replace(redirectUrl);
}
