async function requestLoginToken() {
  const code = searchParam('code');

  await axios.get('/api/afterlogin?code=' + code)
  .then(response => {
    const data = response.data;
    localStorage.setItem('token', data.token);
    localStorage.setItem('userName', data.userName);
    window.location.replace('/');
  }).catch(error => {
    alert(error);
    window.location.replace('/');
  });
}

function searchParam(key) {
  return new URLSearchParams(location.search).get(key);
}

requestLoginToken();
