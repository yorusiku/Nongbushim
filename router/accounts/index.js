const { Container } = require('typedi')
const express = require('express')

const Jelib = Container.get('Jelib')
const passport = Container.get('PassportService')
const UserService = Container.get('UserService')
const router = express.Router()

router.get(`/login`, (req, res) => {
    return res.render('accounts/login')
})

router.get(`/sign_up`, (req, res) => {
    // 소셜 로그인을 통해 받아온 profile 정보 
    let profile = req.signedCookies.profile
    res.clearCookie('profile')
    return res.render('accounts/sign_up', {
        profile: profile,
        validateEmail: function validateEmail(email) {
            const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        }
    })
})

router.get(`/sign_up`, (req, res) => {
    // 소셜 로그인을 통해 받아온 profile 정보 
    let profile = req.signedCookies.profile
    res.clearCookie('profile')
    console.log("!")
    return res.render('accounts/user_info', {
        profile: profile,
        validateEmail: function validateEmail(email) {
            const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        }
    })
})

router.get(`/leave`, async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let userService = new UserService(uid)
    await userService.init()
    return res.render('accounts/leave', {
        user: userService.user
    })
})

router.get(`/verification_code`, (req, res) => {
    return res.render('accounts/verification_code')
})
router.get(`/reset_password`, (req, res) => {
    let requestEmail = req.query.email
    let verifiedEmail = req.signedCookies.verifiedEmail
    return res.render('accounts/reset_password', {
        verifiedEmail: verifiedEmail,
        requestEmail: requestEmail
    })
})

router.post(`/login`, async (req, res) => {
    let userService = new UserService()
    let uid = await userService.signIn(req.body.id, req.body.pw)
    res.cookie('uid', uid, {
        signed: true,
        maxAge: 24000 * 60 * 60 * 365
    })
    res.send()
})

router.get('/kakao', passport.authKakao(), (req, res) => {
    // /oauth/kakao에서 로그인 후 처리
})

module.exports = router