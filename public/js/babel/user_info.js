let __user__ = {};
$(document).ready(() => {
    __user__.strategy = $('#input_username').attr('strategy') || 0;
    // __user__.socialId = $('#input_username').attr('social_id')
});

$('#btn_sign_up').click(() => {
    signUp();
});

function parseUser() {
    __user__.username = $('#input_username').val();
    __user__.nickname = $('#input_nickname').val();
    __user__.password = $('#input_password').val();
    __user__.name = $('#input_name').val();
}

function validUser() {
    if (!validateEmail(__user__.username)) {
        alert('이메일 형식이 올바르지 않습니다.');
        return false;
    }
    if (__user__.strategy == 0) {
        if (__user__.password.length < 6) {
            alert('비밀번호는 6자 이상이어야합니다.');
            return false;
        }
        if (__user__.password.length > 60) {
            alert('비밀번호가 너무 깁니다.');
            return false;
        }
        let passwordConfirm = $('#input_password_confirm').val();
        if (passwordConfirm != __user__.password) {
            alert('입력하신 비밀번호가 확인용 비밀번호와 다릅니다.');
        }
    }
    if (__user__.name.length > 10 && __user__.name.length < 2) {
        alert('성함은 2~10자 이내로 입력 해 주세요.');
        return false;
    }
    if (__user__.nickname.length > 10 && __user__.nickname.length < 2) {
        alert('닉네임은 2~10자 이내로 입력 해 주세요.');
        return false;
    }
    return true;
}

function signUp() {
    parseUser();
    if (validUser()) {
        $.ajax({
            url: '/api/accounts/sign_up',
            type: 'POST',
            data: __user__,
            success: function (result) {
                if (result.code == 200) {
                    if (alert('회원가입이 완료되었습니다.')) {
                        // location.href = '/accounts/login';
                    } else {
                        alert("회원가입에 실패하였습니다.")
                        // location.href = '/accounts/login';
                    }
                } else {
                    alert(result.message);
                }
            }
        });
    }
}