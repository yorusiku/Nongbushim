let farm = {};

$(document).ready(() => {});

function requestCreate() {
    $('#btn_create').attr('disabled', true);
    farm = parseFarm();

    if (isValid(farm)) {
        let formData = new FormData();
        formData.append('imageImg1', getFile('input_file_img1'));
        formData.append('imageImg2', getFile('input_file_img2'));
        formData.append('imageImg3', getFile('input_file_img3'));
        formData.append('imageLogo', getFile('input_file_logo'));
        formData.append('imageOwner', getFile('input_file_owner'));
        formData.append('farm', JSON.stringify(farm));

        $.ajax({
            url: '/api/farms/',
            type: 'POST',
            enctype: 'multipart/form-data',
            data: formData,
            processData: false,
            contentType: false,
            success: function (result) {
                $('#btn_create').attr('disabled', false);
                if (result.code == 200) {
                    alert('농장이 성공적으로 생성되었습니다.');
                    location.href = '/'
                } else {
                    if (result.code == -1003) {
                        location.href = '/accounts/login';
                    }
                    alert(result.message);
                }
            },
            failure: function (result) {
                $('#btn_create').attr('disabled', false);
            }
        });
    }
}

$('#btn_create').click(() => {
    requestCreate();
});

$('.image-upload-container .btn-delete').click(e => {
    hidePreview($(e.target).parent('.image-upload-container').attr('id'));
});