const fs = require('fs');
const sharp = require('sharp');

exports.error = (code, message) => {
    let error = new Error(message);
    error.status = code || 500;
    return error;
}

exports.getUid = (req, res) => {
    let uid = '';
    if (process.env.NODE_ENV != 'production') {
        uid = req.headers.uid
    }
    if (!uid) {
        uid = req.signedCookies.uid || '';
    }
    return uid;
}

exports.responseSuccess = (res, object) => {
    let result = {};
    result.code = 200;
    result['object'] = typeof(object) == "boolean" ? 
        object : object || null;
    result.message = "";
    return res.send(result)
}

exports.responseFail = (res, err) => {
    if (!err.status) {
        console.error(err)
    }
    let result = {};
    result.code = err.status || 500;
    result['object'] = null;
    result.message = err.message;
    result.stack = err.stack;
    return res.send(result)
}

exports.generateRandomString = (length) => {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < length; i++ ) {
       result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

exports.atob = (text) => {
    return Buffer.from(text, 'base64').toString('binary')
}

exports.btoa = (text) => {
    return Buffer.from(text, 'binary').toString('base64')
}

exports.escapeJSON = (param, defaultValue) => {
    if (typeof(param) == 'string') {
        try {
            param = JSON.parse(param);
        } catch (err) {
            param = defaultValue;
        }
    }
    return param;
}

exports.coolFormat = (num) => {
    if (num > 1E6) {
        var si = [
            { value: 1, symbol: "" },
            { value: 1E3, symbol: "k" },
          ];
          if (num > 1E9) {
            si.push({ value: 1E6, symbol: "m" });
          }
          var rx = /\.0+$|(\.[0-9]*[1-9])0+$/;
          var i;
          for (i = si.length - 1; i > 0; i--) {
            if (num >= si[i].value) {
              break;
            }
          }
          var digits = 1;
          return ((num / si[i].value).toFixed(digits).replace(rx, "$1") * 1).toLocaleString()  + si[i].symbol;
    } else {
        return num.toLocaleString();
    }   
}

// 저장될 이미지이름 정하기
exports.getImagePath = (imageFile) => {
    let splitted = imageFile.name.split('.');
    let extension = splitted[splitted.length - 1];
    return `uploads/image_${new Date()*1}_${this.generateRandomString(6)}.${extension}`;
}

exports.safeDeleteFile = function (path) {
    try {
        if (path) {
            fs.unlink(path, (err) => {});
        }
    } catch (err) {
    }
}

exports.parseBody = function (bodyField) {
    if (typeof(bodyField) == 'string') {
        bodyField = JSON.parse(bodyField);
    }
    return bodyField;
}

/**
 * 이미지 처리
 * @param {*} resFilesImage 
 * @return imagePath
 * @error null
 */
exports.processImage = async function (reqFilesImage) {
    let imagePath = null;
    try {
        if (reqFilesImage) {
            imagePath = this.getImagePath(reqFilesImage);
            await reqFilesImage.mv(imagePath);
            await sharp(imagePath).resize(800)
            return '/' + imagePath;
        } else {
            return null;
        }
    } catch (err) {
        console.log(err);
        this.safeDeleteFile(imagePath);
        return null;
    }
}