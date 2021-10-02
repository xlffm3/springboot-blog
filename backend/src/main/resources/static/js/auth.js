async function requestLoginToken() {
  const code = searchParam('code');

  await axios.get('/api/github/login?code=' + code)
  .then(response => {
    const data = response.data;
    localStorage.setItem('token', data.token);
    localStorage.setItem('userName', data.userName);
    window.location.replace('/');
  }).catch(error => {
    if (error.response.status === 400 && error.response.data === '') {
      alert('GitHub Account Setting에서 Email을 설정해주세요.');
      window.location.replace('/');
    }
    if (error.response.status === 400 && error.response.data !== '') {
      alert('해당 SNS 계정과 연동된 회원 정보가 없습니다. 회원 가입 페이지로 이동합니다.');
      sessionStorage.setItem('email', error.response.data);
      window.location.replace('/page/user/register');
    }
  });
}

function searchParam(key) {
  return new URLSearchParams(location.search).get(key);
}

requestLoginToken();
