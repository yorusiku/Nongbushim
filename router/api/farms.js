const { Container } = require('typedi')
const express = require('express')
const fileUpload = require('express-fileupload')
const FarmService = require('../../service/FarmService')

const Jelib = Container.get('Jelib')
const router = express.Router()

// 농장 생성
router.post(`/`, fileUpload(), async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let farmProfile = parseFarmProfile(req)
    let farmService = new FarmService()
    try {
        await farmService.create(uid, farmProfile, req.files)
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 농장 정보 변경
router.put(`/:fid`, fileUpload(), async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let fid = req.params.fid
    let farmProfile = parseFarmProfile(req)
    farmProfile.id = fid
    let farmService = new FarmService(fid)
    try {
        await farmService.update(uid, farmProfile, req.files)
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 농장 삭제
router.delete(`/:fid`, async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let fid = req.params.fid
    let farmService = new FarmService(fid)
    try {
        await farmService.delete(uid)
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 기존 농장에 등록 신청
router.post(`/register/:fid`, async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let fid = req.params.fid
    let job = req.body.job
    let company = req.body.company
    try {
        let farmService = new FarmService(fid)
        await farmService.register(uid, job, company)
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

// 농장의 유저 상태 업데이트
router.put(`/state/:fid`, async (req, res) => {
    let uid = Jelib.getUid(req, res)
    let fid = req.params.fid
    let targetUid = req.body.targetUid
    let state = req.body.state
    try {
        let farmService = new FarmService(fid)
        await farmService.updateState(uid, targetUid, state)
        Jelib.responseSuccess(res, true)
    } catch (err) {
        Jelib.responseFail(res, err)
    }
})

function parseFarmProfile(req) {
    let farm = Jelib.escapeJSON(req.body.farm)
    farm.areaPaths = Jelib.escapeJSON(farm.areaPaths)
    return farm;
}

module.exports = router