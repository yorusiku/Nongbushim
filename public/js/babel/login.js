$('#btn_kakao').click(() => {
    location.href = "/accounts/kakao";
});

$('#btn_login').click(() => {
    login();
});

function login() {
    $.ajax({
        url: `/api/accounts/sign_in`,
        type: 'POST',
        data: {
            username: $('#input_username').val(),
            password: $('#input_password').val()
        },
        success: function (result) {
            if (result.code == 200) {
                location.href = '/';
            } else {
                alert(result.message);
            }
        }
    });
}

$('#input_password').keydown(function (event) {
    var keyCode = event.keyCode ? event.keyCode : event.which;
    if (keyCode == 13) {
        $('#btn_login').trigger('click');
    }
});