const NodeCache = require('node-cache')
const myCache = new NodeCache({
    stdTTL: 60 * 5 // 초 단위
})

exports.set = (key, value) => {
    myCache.set(key, value)
}

exports.get = (key, value) => {
    return myCache.get(key)
}

