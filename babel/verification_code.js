let __stage__ = 0
let __username__ = ''
$(document).ready(() => {
    updateStage()
})

function updateStage() {
    if (__stage__ == 0) {
        $('#container_verify').css('display', 'none')
        $('#container_send').css('display', 'block')
    } else {
        $('#container_verify').css('display', 'block')
        $('#container_send').css('display', 'none')
    }
}

$('#btn_send').click(() => {
    __username__ = $('#input_username').val()
    if (validateEmail(__username__)) {
        $('#btn_send').attr('disabled', true)
        $.ajax({
            url: `/api/accounts/verification_code?email=${__username__}`,
            type: 'POST',
            success: function(result) {
                $('#btn_send').attr('disabled', false)
                if (result.code == 200) {
                    alert('인증번호가 전송되었습니다.')
                    __stage__ = 1
                    updateStage()
                } else {
                    alert(result.message)
                }
            },
            failure: function() {
                $('#btn_send').attr('disabled', false)
            }
        })
    } else {
        alert('이메일 형식이 올바르지 않습니다.')
    }
})

$('#btn_verify').click(() => {
    let code = $('#input_code').val()
    if (code.length == 6) {
        $.ajax({
            url: `/api/accounts/verify_code?code=${code}&email=${__username__}`,
            type: 'POST',
            success: function (result) {
                if (result.code == 200) {
                    alert('인증이 완료되었습니다.')
                    location.href = `/accounts/reset_password?email=${__username__}`
                } else {
                    alert(result.message)
                }
            }
        })
    } else {
        alert('입력하신 인증번호를 확인 해 주세요.')
    }
})