const { Container } = require('typedi')
const Farm = require('../model/Farm')
const FarmUserState = require('../model/FarmUserState')
const Validator = Container.get('Validator')
const Jelib = Container.get('Jelib')
const ErrorCodes = Container.get('ErrorCodes')
const Constants = Container.get('Constants')
const pool = Container.get("pool")

const PAGE_SIZE = 30

module.exports = class FarmService {
    constructor(id) {
        if (id > 0) {
            this.farm = new Farm({ id: id })
        }
    }

    async init() {
        if (this.farm) {
            await this.farm.get()
        }
    }

    // 관련된 농장 목록을 불러온다.
    async getFarmList(uid) {
        // page = page * 1
        // page = isNaN(page) ? 1 : page
        // let pagingClause = `LIMIT ${(page - 1) * PAGE_SIZE}, ${PAGE_SIZE}`
        let query = `
        SELECT f.id, f.name, f.address, fu.job, fu.company, fu.state 
        FROM farms f LEFT JOIN farm_user_states fu ON fu.fid=f.id 
        WHERE fu.uid=? AND deleted=false`
        //  ${pagingClause}`
        let [farms] = await pool.query(query, uid)
        return farms
    }

    async create(uid, farmProfile, reqFiles) {
        if (uid == null || uid.length == 0) {
            throw Jelib.error(
                ErrorCodes.USER_NOT_FOUND,
                "유저를 찾을 수 없습니다. 다시 로그인 해 주세요."
            )
        }
        validateFarm(farmProfile)

        if (reqFiles) {
            farmProfile.img1 = await Jelib.processImage(reqFiles.imageImg1)
            farmProfile.img2 = await Jelib.processImage(reqFiles.imageImg2)
            farmProfile.img3 = await Jelib.processImage(reqFiles.imageImg3)
            farmProfile.imgLogo = await Jelib.processImage(reqFiles.imageLogo)
            farmProfile.imgOwner = await Jelib.processImage(reqFiles.imageOwner)
        }

        this.farm = new Farm(farmProfile)
        await this.farm.insert()
        try {
            await FarmUserState.registerUser({
                fid: this.farm.getId(),
                uid: uid,
                job: Constants.JOBS[0], // 소유자
                state: Constants.FARM_USER_STATE.GRANTED,
                createdAt: new Date(),
                modifiedAt: new Date()
            })
        } catch (err) {
            await this.farm.delete()
            throw err
        }
        
    }

    async update(uid, farmProfile, reqFiles) {
        // 유효성 검사
        this.farm = new Farm(farmProfile)
        let state = await this.getState(uid)
        if (state.state != Constants.FARM_USER_STATE.GRANTED || state.job != Constants.JOBS[0]) {
            throw Jelib.error(
                ErrorCodes.PERMISSION_DENIED,
                "농장을 수정할 권한이 없습니다."
            )
        }
        validateFarm(farmProfile)

        if (reqFiles) {
            farmProfile.img1 = await Jelib.processImage(reqFiles.imageImg1) || farmProfile.img1
            farmProfile.img2 = await Jelib.processImage(reqFiles.imageImg2) || farmProfile.img2
            farmProfile.img3 = await Jelib.processImage(reqFiles.imageImg3 )|| farmProfile.img3
            farmProfile.imgLogo = await Jelib.processImage(reqFiles.imageLogo) || farmProfile.imgLogo
            farmProfile.imgOwner = await Jelib.processImage(reqFiles.imageOwner) || farmProfile.imgOwner
        }

        await this.farm.update(farmProfile)
    }

    async delete(uid) {
        // TODO: 수확등 모든 데이터 삭제할 것인지
        let userState = await FarmUserState.hasPermission(
            this.farm.getId(), uid
        )
        console.log(userState)
        if (Constants.JOBS.indexOf(userState.job) != 0) {
            throw Jelib.error(
                ErrorCodes.PERMISSION_DENIED,
                "농장의 소유자만 농장을 삭제할 수 있습니다."
            )
        }
        await this.farm.delete()
    }

    async register(uid, job, company) {
        let alreadyGranted = await FarmUserState.hasPermission(
            this.farm.getId(), uid
        )
        if (alreadyGranted) {
            throw Jelib.error(
                ErrorCodes.DUPLICATE,
                "이미 해당 농장에 등록된 유저입니다."
            )
        }
        await FarmUserState.registerUser({
            fid: this.farm.getId(),
            uid: uid,
            job: job,
            company: company,
            createdAt: new Date(),
            modifiedAt: new Date()
        })
    }

    async updateState(uid, targetUid, state) {
        let permission = await FarmUserState.hasPermission(
            this.farm.getId(), uid
        )
        if (!permission) {
            throw Jelib.error(
                ErrorCodes.PERMISSION_DENIED,
                "권한이 없습니다."
            )
        }
        await FarmUserState.updateState(
            this.farm.getId(), uid, targetUid, state
        )
    }

    async getStates() {
        return await FarmUserState.getStates(
            this.farm.getId()
        )
    }

    async getState(uid) {
        return await FarmUserState.hasPermission(
            this.farm.getId(),
            uid
        )
    }

    async updateFarmUserState(uid, job, company) {
        return await FarmUserState.update({
            fid: this.farm.getId(),
            uid: uid,
            job: job,
            company: company
        })
    }
}

function validateFarm(farmProfile) {
    if (!Validator.range(farmProfile.name, 1, 20)) {
        throw Jelib.error(
            ErrorCodes.INVALID_FILED,
            "농장명은 20자 이내로 입력 해 주세요."
        )
    }
    if (!Validator.hasMinLength(farmProfile.phone, 1)) {
        throw Jelib.error(
            ErrorCodes.INVALID_FIELD,
            "농장 전화번호를 입력 해 주세요."
        )
    }
    if (!farmProfile.areaPaths) {
        farmProfile.areaPaths = []
    }
}