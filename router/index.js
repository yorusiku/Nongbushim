const { Container } = require('typedi')
const express = require('express')

const passport = Container.get('PassportService')
const UserService = Container.get('UserService')
const router = express.Router()

router.get(`/`, (req, res) => {
    let uid = req.signedCookies.uid || 0
    return res.render('index', {
        uid: uid,
        test: '123'
    })
})

module.exports = router