const { Container } = require('typedi')
const express = require('express')

const passport = Container.get('PassportService')
const UserService = Container.get('UserService')
const FarmService = Container.get('FarmService')
const router = express.Router()


router.get(`/`, async (req, res) => {
    let uid = req.signedCookies.uid || 0
    let userService = new UserService(uid)
    let farmService = new FarmService(uid)
    await userService.init()
    let farms = await farmService.getFarmList(uid, 1)
    if (uid==0) {
        return res.redirect('/accounts/login')
    }
    
    return res.render('index', {
        uid: userService.user.id,
        user_name: userService.user.name,
        login: userService.user.id != 0,
        farms: farms
    })
})

router.get(`/ui`, async (req, res) => {
    let uid = req.signedCookies.uid || 0
    let userService = new UserService(uid)
    await userService.init()
    if (uid==0) {
        return res.redirect('/accounts/login')
    }
    
    return res.render('index_test', {
        uid: userService.user.id,
        user_name: userService.user.name,
        login: userService.user.id != 0
    })
})

module.exports = router