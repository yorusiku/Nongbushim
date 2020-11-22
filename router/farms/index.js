const { Container } = require('typedi')
const express = require('express')

const Jelib = Container.get('Jelib')
const passport = Container.get('PassportService')
const router = express.Router()

const FarmService = require('../../service/FarmService')
const UserService = require('../../service/UserService')

router.get(`/create`, async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let userService = new UserService(uid)
    await userService.init()
    return res.render('farms/create', {
        uid: userService.user.id,
        user_name: userService.user.name,
        login: userService.user.id != 0
    })
})

router.get(`/modify/:fid`, async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let farmService = new FarmService(req.params.fid)
    let userService = new UserService(uid)
    let [state] = await Promise.all([
        farmService.getState(uid),
        farmService.init(),
        userService.init()
    ])
    return res.render('farms/modify', {
        uid: userService.user.id,
        user_name: userService.user.name,
        login: userService.user.id != 0,
        state: state,
        farm: farmService.farm,
    })
})

module.exports = router