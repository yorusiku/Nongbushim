

function leave() {
    let password = $('#input_password').length > 0 ? 
    $('#input_password').val() : ''
    let name = $('#input_name').length > 0 ?
    $('#input_name').val() : ''

    $.ajax({
        url: '/api/accounts/leave',
        type: 'POST',
        data: {
            name: name,
            password: password
        },
        success: function (result) {
            if (result.code == 200) {
                alert('회원탈퇴가 완료되었습니다. 이용 해 주셔서 감사합니다.')
                location.href = '/accounts/login'
            } else {
                alert(result.message)
            }
        }
    })
}

$('#btn_leave').click(() => {
    leave()
})