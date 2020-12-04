/**
 * ================
 * Daum Map
 * ================
 */
var mapContainer = document.getElementById('map'),
    // 지도를 표시할 div  
mapOption = {
    center: new kakao.maps.LatLng(33.450701, 126.570667), // 지도의 중심좌표
    level: 3 // 지도의 확대 레벨
};

var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다

var drawingFlag = false; // 다각형이 그려지고 있는 상태를 가지고 있을 변수입니다
var drawingPolygon; // 그려지고 있는 다각형을 표시할 다각형 객체입니다
var polygon; // 그리기가 종료됐을 때 지도에 표시할 다각형 객체입니다
var areaOverlay; // 다각형의 면적정보를 표시할 커스텀오버레이 입니다

let polygonOption = {
    strokeWeight: 3,
    strokeColor: '#00a0e9',
    strokeOpacity: 1,
    strokeStyle: 'solid',
    fillColor: '#00a0e9',
    fillOpacity: 0.2
};

function centerMap(address) {
    var geocoder = new kakao.maps.services.Geocoder();
    geocoder.addressSearch(address, function (result, status) {
        if (status == kakao.maps.services.Status.OK) {
            var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
            map.setCenter(coords);
            console.log(coords);
        }
    });
}

// 지도에 마우스 클릭 이벤트를 등록합니다
// 지도를 클릭하면 다각형 그리기가 시작됩니다 그려진 다각형이 있으면 지우고 다시 그립니다
kakao.maps.event.addListener(map, 'click', function (mouseEvent) {
    var clickPosition = mouseEvent.latLng;
    if (!drawingFlag) {
        drawingFlag = true;
        if (polygon) {
            polygon.setMap(null);
            polygon = null;
        }
        if (areaOverlay) {
            areaOverlay.setMap(null);
            areaOverlay = null;
        }
        drawingPolygon = new kakao.maps.Polygon(Object.assign({
            map: map, // 다각형을 표시할 지도입니다
            path: [clickPosition] // 다각형을 구성하는 좌표 배열입니다 클릭한 위치를 넣어줍니다
        }, polygonOption));

        // 그리기가 종료됐을때 지도에 표시할 다각형을 생성합니다 
        polygon = new kakao.maps.Polygon(Object.assign({
            path: [clickPosition]
        }, polygonOption));
    } else {
        // 다각형이 그려지고 있는 상태이면 
        var drawingPath = drawingPolygon.getPath();
        drawingPath.push(clickPosition);
        drawingPolygon.setPath(drawingPath);
        var path = polygon.getPath();
        path.push(clickPosition);
        polygon.setPath(path);
    }
});

// 지도에 마우스무브 이벤트를 등록합니다
// 다각형을 그리고있는 상태에서 마우스무브 이벤트가 발생하면 그려질 다각형의 위치를 동적으로 보여주도록 합니다
kakao.maps.event.addListener(map, 'mousemove', function (mouseEvent) {
    if (drawingFlag) {
        var mousePosition = mouseEvent.latLng;
        var path = drawingPolygon.getPath();
        if (path.length > 1) {
            path.pop();
        }
        // 마우스의 커서 위치를 좌표 배열에 추가합니다
        path.push(mousePosition);
        drawingPolygon.setPath(path);
    }
});

// 지도에 마우스 오른쪽 클릭 이벤트를 등록합니다
// 다각형을 그리고있는 상태에서 마우스 오른쪽 클릭 이벤트가 발생하면 그리기를 종료합니다
kakao.maps.event.addListener(map, 'rightclick', function (mouseEvent) {
    if (drawingFlag) {
        drawingPolygon.setMap(null);
        drawingPolygon = null;
        var path = polygon.getPath();
        if (path.length > 2) {
            polygon.setMap(map);

            var area = Math.round(polygon.getArea()),
                // 다각형의 총면적을 계산합니다
            content = '<div class="info">총면적 <span class="number"> ' + area + '</span> m<sup>2</sup></div>'; // 커스텀오버레이에 추가될 내용입니다
            areaOverlay = new kakao.maps.CustomOverlay({
                map: map,
                content: content,
                xAnchor: 0,
                yAnchor: 0,
                position: path[path.length - 1]
            });
        } else {
            polygon = null;
        }
        drawingFlag = false;
    }
});

/**
 * ================
 * Daum Postcode
 * ================
 */
function openPostcodeModal() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분입니다.
            // 예제를 참고하여 다양한 활용법을 확인해 보세요.
            $('#input_post_code').val(data.zonecode);
            $('#input_address').val(data.roadAddress);
            centerMap(data.roadAddress);
        }
    }).open();
}

/**
 * ================
 * 이미지 관리
 * ================
 */
$('.image-upload-container').click(e => {
    if (!$(e.target).hasClass('btn-delete')) {
        $(e.target).next('input[type=file]').trigger('click');
    }
});

function showPreview(event, imageContainerId) {
    var imagePreview = $(`#${imageContainerId} img`);
    imagePreview.attr('src', URL.createObjectURL(event.target.files[0]));
    imagePreview.css('display', 'block');
    $(`#${imageContainerId} .btn-delete`).css('display', 'inline-block');
    $(`#${imageContainerId} span`).css('display', 'none');
    imagePreview.onload = function () {
        URL.revokeObjectURL(imagePreview.src); // free memory
    };
}

function hidePreview(imageContainerId) {
    $(`#${imageContainerId} .btn-delete`).css('display', 'none');
    $(`#${imageContainerId} span`).css('display', 'block');
    $(`#${imageContainerId} img`).css('display', 'none');
    $(`#${imageContainerId}`).next('input[type=file]').files = null;
}

/**
 * ================
 * 리퀘스트
 * ================
 */
function getFile(inputId) {
    let target = document.getElementById(inputId);
    if (target.files) {
        return target.files[0];
    } else {
        return '';
    }
}

function parseFarm() {
    let farm = {};
    farm.name = $('#input_name').val();
    farm.postCode = $('#input_post_code').val();
    farm.address = $('#input_address').val();
    farm.addressDetail = $('#input_address_detail').val();
    if (polygon != null) {
        farm.areaPaths = polygon.getPath().map(d => {
            let keys = Object.keys(d);
            return {
                lat: Math.min(d[keys[0]], d[keys[1]]),
                lng: Math.max(d[keys[0]], d[keys[1]])
            };
        });
    } else {
        farm.areaPaths = [];
    }
    farm.phone = $('#input_phone').val();
    farm.ownerName = $('#input_owner_name').val();
    farm.corpNumber = $('#input_corp_number').val();
    farm.web = $('#input_web').val();
    farm.sector = $('input[name="input_sector"]:checked').val();
    farm.subSector = $('#input_sub_sector').val();
    console.log(farm)
    return farm;
}

function isValid(farm) {
    if (farm.name.length < 1 || farm.name.length > 20) {
        alert('농장명은 1~20자이내여야합니다.');
        return false;
    }
    if (farm.phone.length < 1) {
        alert('전화번호를 입력 해 주세요.');
        return false;
    }
    if (farm.phone.length > 20) {
        alert('전화번호가 너무 깁니다.');
        return false;
    }
    return true;
}