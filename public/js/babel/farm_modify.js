let __farm__ = {};
$(document).ready(() => {
    __farm__ = JSON.parse($('#farm_data').text());
    console.log(__farm__)
    centerMap(__farm__.address);
    drawArea();
    setImages();
});

function drawArea() {
    let paths = [];
    for (var i = 0; i < __farm__.areaPaths.length; i++) {
        paths.push(new kakao.maps.LatLng(__farm__.areaPaths[i].lat, __farm__.areaPaths[i].lng));
    }
    polygon = new kakao.maps.Polygon(Object.assign({
        path: paths
    }, polygonOption));

    polygon.setMap(map);
}

function setImages() {
    setImage(__farm__.img1, '#img1_preview');
    setImage(__farm__.img2, '#img2_preview');
    setImage(__farm__.img3, '#img3_preview');
    setImage(__farm__.imgLogo, '#logo_preview');
    setImage(__farm__.imgOwner, '#owner_preview');
}

function setImage(value, previewId) {
    if (value) {
        $(previewId).find('span').css('display', 'none');
        $(previewId).find('.btn-delete').css('display', 'inline-block');
        $(previewId).find('img').attr('src', value);
        $(previewId).find('img').css('display', 'block');
    }
}

$('.image-upload-container .btn-delete').click(e => {
    hidePreview($(e.target).parent('.image-upload-container').attr('id'));
    let fieldName = $(e.target).attr('field_name');
    __farm__[fieldName] = null;
});

$('#btn_modify').click(() => {
    requestModify();
});

function requestModify() {
    $('#btn_modify').attr('disabled', true);
    Object.assign(__farm__, parseFarm());
    console.log(__farm__);
    if (isValid(__farm__)) {
        let formData = new FormData();
        formData.append('imageImg1', getFile('input_file_img1'));
        formData.append('imageImg2', getFile('input_file_img2'));
        formData.append('imageImg3', getFile('input_file_img3'));
        formData.append('imageLogo', getFile('input_file_logo'));
        formData.append('imageOwner', getFile('input_file_owner'));
        formData.append('farm', JSON.stringify(__farm__));
        $.ajax({
            url: '/api/farms/' + __farm__.id,
            type: 'PUT',
            enctype: 'multipart/form-data',
            data: formData,
            processData: false,
            contentType: false,
            success: function (result) {
                $('#btn_create').attr('disabled', false);
                if (result.code == 200) {
                    alert('농장이 성공적으로 수정되었습니다.');
                    // location.href = '/';
                    $('#btn_create').attr('disabled', false);
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