
$('#btn_logout').click(() => {
    logout()
})

function logout() {
    console.log(getCookie('uid'))
    $.ajax({
        url: `/api/accounts/sign_out`,
        type: 'POST',
        data: {
            uid: getCookie('uid')
        },
        success: function(result) {
            if (result.code == 200) {
                location.href = '/accounts/login'
            } else {
                alert(result.message)
            }
        }
    })
}

var getCookie = function(name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value? value[2] : null;
    }
