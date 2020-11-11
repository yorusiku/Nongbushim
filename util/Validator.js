/**
 * 최소 N자를 충족하는 문자열인지 체크
 * @param {*} text 
 * @param {*} minLength 
 */
exports.hasMinLength = (text, minLength) => {
    minLength = minLength || 1
    return text != null && text.length >= minLength
}

/**
 * 길이가 range에 포함되는지 체크
 * @param {*} text 
 * @param {*} minLength 
 */
exports.range = (text, minLenth, maxLength) => {
    return this.hasMinLength(text, minLenth) && text.length <= maxLength
}

exports.isEmail = (text) => {
    let re = /\S+@\S+\.\S+/;
    return re.test(text);
}

