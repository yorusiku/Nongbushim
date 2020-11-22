const { Container } = require('typedi')
const pool = Container.get('pool')
const Jelib = Container.get('Jelib')

/*
# 농장 테이블 (farms)
* id *농장 고유 ID - INT 
* name *농장 이름 - VARCHAR(20)
* address 농장 주소 - VARCHAR(100)
* areaPaths 다각형 영역 좌표 배열 - TEXT
* phone *전화번호 - VARCHAR(20)
* ownerName 농장주인명 - VARCHAR(10)
* corpNumber 법인 번호 - VARCHAR(20)
* web 사이트 주소 - VARCHAR(100)
* createdAt 생성일 - INT
*/
module.exports = class Farm {
    constructor(farmObject) {
        Object.assign(this, farmObject)
    }

    async get() {
        if (this.id) {
            let [[ farm ]] = await pool.query(`
                SELECT * FROM farms WHERE id=? LIMIT 1
            `, this.id)
            if (farm) {
                farm.areaPaths = Jelib.escapeJSON(farm.areaPaths)
                Object.assign(this, farm)
            } else {
                this.id = 0
            }
        }
    }

    async insert() {
        delete this.id
        this.createdAt = new Date()
        this.modifiedAt = new Date()
        this.areaPaths = JSON.stringify(this.areaPaths)
        let [result] = await pool.query(`
            INSERT INTO farms SET ?;
        `, this)
        this.id = result.insertId
    }

    async delete() {
        // TODO: 농장 삭제
        if (this.id) {
            await pool.query(`
                DELETE FROM farms WHERE id=? LIMIT 1
            `, this.id)
        }
    }

    async update(farmProfile) {
        Object.assign(this, farmProfile)
        delete this["createdAt"]
        this["modifiedAt"] =  new Date(Date.parse(this.modifiedAt));
        this.areaPaths = JSON.stringify(this.areaPaths)
        await pool.query(`
            UPDATE farms SET ? WHERE id=? LIMIT 1
        `, [this, this.id])
    }

    getId() { return this.id }

    getName() { return this.name }

    getAddress() { return this.address }

    getAreaPaths() {
        this.areaPaths = Jelib.escapeJSON(this.areaPaths)
        return this.areaPaths
    }

    getPhone() {
        return this.phone
    }

    getOwnerName() {
        return this.ownerName
    }

    getCorpNumber() { return this.corpNumber }

    getWeb() { return this.web }
}