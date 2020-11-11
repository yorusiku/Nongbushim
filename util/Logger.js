exports.e = (message, where) => {
    if (message) {
        if (typeof(message) == 'string') {
            console.error(`[Error] ${message} :: ${where}`)
        } else {
            // Error 객체
            console.error(`[Error] ${message.stack}`)
        }
    }
}

exports.d = (message, where) => {
    console.log(`[Debug] ${message} ${where ? '::' + where : ''}`)
}