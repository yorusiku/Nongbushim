const mysql = require('mysql2/promise')
const NodeCache = require('node-cache')
const Constants = require('./Constants')
const { Container } = require('typedi')

exports.inject = () => {
    Container.set("pool", mysql.createPool(getDatabaseOption()))
    Container.set("cache", new NodeCache({ stdTTL: 60 * 2}))

    Container.set("Validator", require('./util/Validator'))
    Container.set("ErrorCodes", require('./ErrorCodes'))
    Container.set("Constants", require('./Constants'))
    Container.set("Logger", require('./util/Logger'))
    Container.set("Jelib", require('./util/Jelib'))
    Container.set("Cache", require('./util/Cache'))

    Container.set('PassportService', require('./service/PassportService'))
    Container.set('UserService', require('./service/UserService'))
    Container.set('FarmService', require('./service/FarmService'))
}

function getDatabaseOption() {
    let config = Constants.DATABASE
    if (process.env.NODE_ENV == 'production') {
        // TODO: Production Configure
    }
    return config
}