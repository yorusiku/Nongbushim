const assert = require('assert')
const { Container } = require('typedi')
const di = require('../di')
di.inject()
const UserService = require('../service/UserService')
const Validator = require('../util/Validator')

describe('signUp', function() {
	it('회원가입 함', async function () { 
        di.inject()
        let userService = new UserService(0, Container)
        
        let userObject = {
            username: 'lx5475@naver.com',
            password: 'ckddnjs1',
            name: 'JieunPark',
            strategy: 2
        }

        await userService.signUp(userObject)

        assert.equal(1, [1,2,3].indexOf(0));
    });
});

describe('passwordSignIn', function() {
	it('비밀번호 비교', async function () { 
        di.inject()
        let userService = new UserService(1, Container)
        await userService.init()

        assert.equal(true, await userService.signIn2(1, 'ckddnjs1'));
    });
});

describe('resetPassword', function() {
	it('비밀번호 재설정', async function () { 
        let username = 'gold24park@gmail.com'
        let password = 'parkz119'
        let userService = new UserService()
        await userService.resetPassword(username, password)

    });
});