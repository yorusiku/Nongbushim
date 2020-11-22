const { Container } = require('typedi')
const bcrypt = require('bcrypt')
const pool = Container.get("pool")
const Jelib = Container.get('Jelib')

const SALT_ROUND = 10

/** 
# 회원 테이블 (users)
* id *회원 고유 ID - VARCHAR(30)
* username *접속 아이디/socialId - VARCHAR(20)
* password 접속 비밀번호 - VARCHAR(255)
* name *이름 -  VARCHAR(10)
* strategy *가입경로 - int(10)
* createdAt 가입일 - int(10)
*/

module.exports = class User {

    constructor(userObject) {
        Object.assign(this, userObject)
    }

    async get() {
        let fieldName = ''
        let value = ''
        for (var i = 0; i < Object.keys(this).length; i++) {
            let key = Object.keys(this)[i]
            if (key == 'id' && this[key]) {
                fieldName = key
                value = this[key]
                break;
            }
            if (key == 'socialId' && this[key]) {
                fieldName = key
                value = this[key]
                break;
            }
            if (key == 'username' && this[key]) {
                fieldName = key
                value = this[key]
                break;
            }
        }
        let [[ user ]] = await pool.query(`
            SELECT * FROM users WHERE 
            ${fieldName}=?
            LIMIT 1
        `, value)
        if (user) {
            Object.assign(this, user)
        } else {
            this.id = null // Empty 처리
        }
    }

    hashPassword() {
        if (this.password) {
            let salt = bcrypt.genSaltSync(SALT_ROUND)
            this.password = bcrypt.hashSync(this.password, salt)
        }
    }

    /**
     * 비밀번호 검사
     * @param {*} password 
     * @return uid
     */
    matchPassword(password) {
        let hasPassword = password && this.password
        if (!this.isEmpty() && hasPassword && bcrypt.compareSync(password, this.password)) {
            return this.id
            return null
        } else {
        }
    }

    async delete() {
        // 회원 탈퇴
        let [result] = await pool.query(`
            DELETE FROM users WHERE id=? LIMIT 1
        `, this.id)
    }

    async insert() {
        this.user_id = Jelib.generateRandomString(30)
        this.createdAt = new Date()
        this.modifiedAt = new Date()
        this.hashPassword()
        console.log(this)
        await pool.query(`
            INSERT INTO users SET ?
        `, this)
        return this.id
    }

    isEmpty() {
        return !this.id 
    }

    getId() { return this.id }

    getUsername() { return this.username }

    getPassword() { return this.password }

    getName() { return this.name }

    getNickname() { return this.nickname }

    getStrategy() { return this.strategy }

    getCreatedAt() { return this.createdAt }

    getModifiedAt() { return this.modifiedAt }

    async resetPassword(password) {
        this.password = password
        this.hashPassword()
        await pool.query(`
            UPDATE users SET password=? WHERE username=? LIMIT 1
        `, [this.password, this.username])
    }
}