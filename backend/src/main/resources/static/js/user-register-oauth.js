function initiate() {
  document.getElementById('email').value = sessionStorage.getItem('email');
}

async function sendRequest() {
  const name = document.getElementById('name').value;
  const email = document.getElementById('email').value;
  const json = JSON.stringify({name: name, email: email});
  await axios.post('/api/users/oauth', json, {
    headers: {
      'Content-Type': 'application/json'
    }
  }).then(response => {
    alert('회원 가입이 완료되었습니다. 다시 로그인해주세요.');
    sessionStorage.removeItem('email');
    window.location.replace('/')
  }).catch(error => {
    alert(error.response.data);
  });
}

function activateSubmitButton() {
  const $button = document.getElementById('submit-button');
  $button.addEventListener('click', ev => {
    sendRequest();
  });
}

initiate();
activateSubmitButton();
