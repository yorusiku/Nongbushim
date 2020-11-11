const { Container } = require('typedi')
const express = require('express')
const VerificationService = require('../../service/VerificationService')

const passport = Container.get('PassportService')
const UserService = Container.get('UserService')
const Constants = Container.get('Constants')
const Jelib = Container.get('Jelib')
const router = express.Router()

// 이메일 로그인
router.post(`/sign_in`, async (req, res) => { 
    let username = req.body.username
    let password = req.body.password
    
    try {
        let userService = new UserService()
        let uid = await userService.signIn(username, password)
        res.cookie('uid', uid, {
            signed: true,
            maxAge: 24000 * 60 * 60 * 365
        })
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 회원가입
router.post(`/sign_up`, async (req, res) => {
    let userProfile = {
        username: req.body.username,
        password: req.body.password,
        name: req.body.name,
        strategy: req.body.strategy || Constants.STRATEGY.EMAIL,
        socialId: req.body.socialId
    }
    try {
        let userService = new UserService()
        await userService.signUp(userProfile)
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 이메일로 인증코드 전송
router.post(`/verification_code`, async (req, res) => {
    let email = req.query.email
    let service = new VerificationService(email)
    try {
        await service.sendVerificationCode()
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 인증코드 검증
router.post(`/verify_code`, async (req, res) => {
    let email = req.query.email
    let code = req.query.code
    let service = new VerificationService(email)
    try {
        await service.verifyCode(code)
        res.cookie('verifiedEmail', email, {
            signed: true,
            maxAge: 60 * 10 * 1000 // 10분
        })
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 비밀번호 재설정
router.post(`/reset_password`, async (req, res) => {
    let username = req.signedCookies.verifiedEmail
    let password = req.body.password
    let service = new UserService()
    try {
        await service.resetPassword(username, password)
        res.clearCookie('verifiedEmail')
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 회원탈퇴
router.post(`/leave`, async (req, res) => {
    let uid = Jelib.getUid(req)
    let service = new UserService(uid)
    let name = req.body.name
    let password = req.body.password
    try {
        await service.init()
        await service.leave(name, password)
        res.clearCookie('uid')
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

module.exports = router