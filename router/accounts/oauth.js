const { Container } = require('typedi')
const express = require('express')

const passport = Container.get('PassportService')
const UserService = Container.get('UserService')
const router = express.Router()


router.get('/kakao', passport.authKakao(), (req, res) => {
    // req.user: {"username":1507195764,"strategy":1, "id":0}
    let user = req.user
    req.logout()
    if (!user.getId()) {
        // 회원가입이 필요할 경우 받은 정보 sign_up으로 전송
        res.cookie('profile', user, {
            signed: true,
        })
        res.redirect('../sign_up')
    } else {
        // 로그인 후 redirect_uri로 이동
        res.cookie('uid', user.getId(), {
            signed: true,
            maxAge: 24000 * 60 * 60 * 365
        })
        let redirectUri = req.query.redirect_uri || '/'
        res.redirect(redirectUri)
    }
})

module.exports = router