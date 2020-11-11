var colorCodes = {
    "Beige": "#A99E82",
    "Black": "#191919",
    "Light blue": "#11B0F4",
    "Blue": "#3042EC",
    "Brown": "#6C5843",
    "Colorful": "#ff5050~#676ed2",
    "Red": "#ff5050",
    "Pink": "#FE98C5",
    "Orange": "#FF8B28",
    "White": "#FBFBFB",
    "Yellow": "#FAEB3C",
    "Green": "#3FCC5E",
    "Gray": "#808188",
    "Purple": "#7C20E8",
    "Aqua": "#1FD2DE"
}

var labelColorCodes = {
    "Beige": "#FFF",
    "Black": "#FFF",
    "Light blue": "#FFF",
    "Blue": "#FFF",
    "Brown": "#FFF",
    "Colorful": "#FFF",
    "Red": "#FFF",
    "Pink": "#FFF",
    "Orange": "#FFF",
    "White": "#3B3A34",
    "Yellow": "#3B3A34",
    "Green": "#FFF",
    "Gray": "#FFF",
    "Purple": "#FFF",
    "Aqua": "#FFF"
}

function updateColorCodes() {
    $('.color-box').each(function (index, elem) {
        let color = $(elem).text().trim();
        if (color == 'White') {
            $(elem).css('border', '1px solid #ddd');    
        } else {
            $(elem).css('border', 'none');    
        }
        $(elem).css('color', labelColorCodes[color]);
        if (color == 'Colorful') {
            var colors = colorCodes[color].split('~');
            $(elem).css('background', 'linear-gradient(90deg, ' + colors[0] + 
            ', ' + colors[1] + ')');
        } else {
            $(elem).css('background-color', colorCodes[color]);
        }
        
    })
}
