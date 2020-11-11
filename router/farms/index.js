const { Container } = require('typedi')
const express = require('express')

const Jelib = Container.get('Jelib')
const passport = Container.get('PassportService')
const router = express.Router()

const FarmService = require('../../service/FarmService')

router.get(`/create`, (req, res) => {
    return res.render('farms/create')
})

router.get(`/modify/:fid`, async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let farmService = new FarmService(req.params.fid)
    let [state] = await Promise.all([
        farmService.getState(uid),
        farmService.init()
    ])
    return res.render('farms/modify', {
        farm: farmService.farm,
        state: state,
    })
})

module.exports = router