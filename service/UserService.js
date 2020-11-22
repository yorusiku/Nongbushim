
const User = require('../model/User')
const { Container } = require('typedi')
const Validator = Container.get("Validator")
const ErrorCodes = Container.get("ErrorCodes")
const Constants = Container.get('Constants')
const Jelib = Container.get("Jelib")

module.exports = class UserService {
    constructor(id) {
        if (id) {
            this.user = new User({ id: id })
        }    
    }

    async init() {
        if (this.user) {
            await this.user.get()
        }
    }

    async signIn(username, password) {
        this.user = new User({ username: username })
        console.log(this.user)
        await this.user.get()
        if (this.user.isEmpty()) {
            throw Jelib.error(
                ErrorCodes.USER_NOT_FOUND,
                "존재하지 않는 아이디입니다."
            )
        }
        let uid = this.user.matchPassword(password)
        if (!uid) {
            if (this.user.getStrategy() != Constants.STRATEGY.EMAIL) {
                throw Jelib.error(
                    ErrorCodes.BAD_REQUEST,
                    "소셜 계정으로 가입한 회원입니다."
                )
            } else {
                throw Jelib.error(
                    ErrorCodes.WRONG_PASSWORD,
                    "비밀번호가 올바르지 않습니다."
                )
            }
        }
        return uid
    }

    async signUp(userObject) {
        this.user = new User(userObject)

        // 유효성 검사
        if (!Validator.range(this.user.getName(), 2, 10)) {
            throw Jelib.error(
                ErrorCodes.INVALID_FILED,
                "성함은 2~10자로 입력 해 주세요."
            )
        }

        // 유효성 검사
        if (!Validator.range(this.user.getName(), 2, 10)) {
            throw Jelib.error(
                ErrorCodes.INVALID_FILED,
                "성함은 2~10자로 입력 해 주세요."
            )
        }
        
        let duplicateUser = new User({ username: this.user.getUsername() })
        await duplicateUser.get()
        if (!duplicateUser.isEmpty()) {
            throw Jelib.error(
                ErrorCodes.DUPLICATE,
                "이미 사용중인 이메일 주소입니다."
            )
        }
        
        await this.user.insert()
    }

    async resetPassword(username, password) {
        this.user = new User({ username: username })
        await this.user.resetPassword(password)
    }

    // 회원탈퇴
    async leave(name, password) {
        if (this.user.getStrategy() == Constants.STRATEGY.EMAIL) {
            if (!this.user.matchPassword(password)) {
                throw Jelib.error(
                    ErrorCodes.WRONG_PASSWORD,
                    "비밀번호가 올바르지 않습니다."
                )
            }
        } else {
            if (this.user.getName() != name) {
                throw Jelib.error(
                    ErrorCodes.INVALID_FIELD,
                    "이름이 일치하지 않습니다."
                )
            }
        }
        // TODO: 유저가 관리중인 농장에 유저 혼자뿐일경우 농장도 모두 삭제할건지
        if (this.user) {
            await this.user.delete()
        }
    }
}