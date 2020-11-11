let __password__ = ''

$(document).ready(() => {
    if ($('#data').attr('verified_email') != $('#data').attr('request_email')) {
        alert('잘못된 접근입니다.')
        location.href = '/accounts/verification_code'
    }
})

function isValid() {
    __password__ = $('#input_password').val()
    let passwordConfirm = $('#input_password_confirm').val()
    if (__password__.length < 6) {
        alert('비밀번호는 6자 이상이어야합니다.')
        return false
    }
    if (__password__.length > 60) {
        alert('비밀번호가 너무 깁니다.')
        return false
    }
    if (__password__ != passwordConfirm) {
        alert('입력하신 비밀번호가 확인용 비밀번호와 다릅니다.')
    }
}

$('#btn_reset').click(() => {
    resetPassword()
})

function resetPassword() {
    $('#btn_reset').attr('disabled', true)
    $.ajax({
        url: '/api/accounts/reset_password',
        type: 'POST',
        body: {
            password: __password__
        },
        success: function (result) {
            $('#btn_reset').attr('disabled', false)
            if (result.code == 200) {
                alert('비밀번호 재설정이 완료되었습니다.')   
                location.href = '/accounts/login'
            } else {
                alert(result.message)
            }
        },
        failure: function () {
            $('#btn_reset').attr('disabled', false)
        }
    })
}