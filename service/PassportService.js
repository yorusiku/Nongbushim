const { Container } = require('typedi')
const passport = require('passport')
const NaverStrategy = require('passport-naver').Strategy
const KakaoStrategy = require('passport-kakao').Strategy

const User = require('../model/User')

const Constants = Container.get('Constants')

passport.serializeUser((user, done) => {
    done(null, user)
})
passport.deserializeUser((object, done) => {
    done(null, object)
})

passport.use('kakao', new KakaoStrategy({
    clientID: Constants.KAKAO_PASSPORT.CLIENT_ID,
    clientSecret: Constants.KAKAO_PASSPORT.CLIENT_SECRET,
    callbackURL: Constants.KAKAO_PASSPORT.CALLBACK_URL
}, async (accessToken, refreshToken, profile, done) => {
    if (accessToken && profile) {
        let user = new User({
            username: profile._json.kaccount_email,
            strategy: Constants.STRATEGY.KAKAO,
            socialId: profile._json.id
        })
        await user.get()
        return done(null, user)
    } else {
        return done(new Error('AccessToken Not Found'), null)
    }
}))

// TODO: 네이버는 검수 후 처리 - 차차차참고
class PassportService {
    constructor() {
        passport.initialize()
    }

    authKakao() {
        return passport.authenticate('kakao')
    }
}

module.exports = new PassportService()