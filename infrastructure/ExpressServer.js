const express = require('express')
const bodyParser = require('body-parser')
const cookieParser = require('cookie-parser')
const helmet = require('helmet')
const path = require('path')
const async = require('async')
const passport = require('passport')
const { Container } = require('typedi')
const Constants = Container.get('Constants')

module.exports = class ExpressServer {
    constructor() {
        this.app = express();
    }

    init() {
        this.app.use('/uploads', express.static('uploads'));
        this.app.set('views', path.join(__dirname, '../', 'public', 'views'));
        this.app.set('view engine', 'pug');
        this.app.use(parallel([
            helmet({
                contentSecurityPolicy: false
            }),
            passport.initialize(),
            express.static(path.join(__dirname, '../', 'public')),
            cookieParser(Constants.COOKIE_SECRET),
            bodyParser.json({ limit: '50mb' }),
            bodyParser.urlencoded({ extended: false, limit: '50mb' }),
        ]));

        this.setRouters()

        return this.app;
    }

    setRouters() {
        this.app.use('/', require('../router/index'))
        this.app.use('/accounts', require('../router/accounts/index'))
        this.app.use('/accounts/oauth', require('../router/accounts/oauth'))
        this.app.use('/farms', require('../router/farms/index'))

        this.app.use('/api/accounts', require('../router/api/accounts'))
        this.app.use('/api/farms', require('../router/api/farms'))
    }
}

function parallel(middlewares) {
    return function (req, res, next) {
        async.each(middlewares, function (mw, cb) {
            mw(req, res, cb);
        }, next);
    };
}