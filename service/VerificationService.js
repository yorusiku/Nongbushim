
const nodemailer = require('nodemailer')
const { Container } = require('typedi')

const Constants = Container.get('Constants')
const ErrorCodes = Container.get('ErrorCodes')
const Cache = Container.get('Cache')
const Jelib = Container.get('Jelib')
const User = require('../model/User')

const transporter = nodemailer.createTransport({
    service: 'gmail',
    port: 465,
    host: 'smtp.gmail.com',
    secure: true,
    auth: {
        user: Constants.EMAIL.username,
        pass: Constants.EMAIL.password
    }
})

module.exports = class VerificationService {
    constructor(email) {
        this.email = email
    }

    async sendVerificationCode() {
        // 소셜 계정인지 체크
        let user = new User({ username: this.email })
        await user.get()
        if (user.getStrategy() != Constants.STRATEGY.EMAIL) {
            throw Jelib.error(
                ErrorCodes.BAD_REQUEST,
                "소셜 계정으로 가입한 회원입니다."
            )
        }
        let code = createVerifiationCode(this.email)
        await sendEmail(
            this.email,
            "[농부심 ERP] 비밀번호 재설정을 위한 인증코드를 안내해드립니다.",
            generateVerificationCodeEmailHtml(this.email, code)
        )
    }

    verifyCode(code) {
        if (this.email) {
            let cacheCode = Cache.get(this.email)
            if (!cacheCode) {
                throw Jelib.error(
                    ErrorCodes.TIME_OUT,
                    "인증시간이 만료되었습니다."
                )
            }
            if (cacheCode != code) {
                throw Jelib.error(
                    ErrorCodes.VERIFICATION_FAILED,
                    "인증번호가 올바르지 않습니다."
                )
            }
            return true
        } else {
            throw Jelib.error(
                ErrorCodes.USER_NOT_FOUND,
                "이메일 정보를 찾을 수 없습니다."
            )
        }
    }
}

function createVerifiationCode(email) {
    // 이메일 코드 생성, 캐시 시간은 5:00 분
    let code = generateRandomString(6)
    if (email) {
        Cache.set(email, code)
        return code
    } else {
        throw Jelib.error(
            ErrorCodes.USER_NOT_FOUND,
            "이메일 정보를 찾을 수 없습니다."
        )
    }
}

/**
 * 이메일 보내기
 * @param {*} to 수신자 이메일
 * @param {*} subject 제목
 * @param {*} html 내용 html
 */
function sendEmail(to, subject, html) {
    var mailOptions = {
        from: transporter.options.auth.user,
        to: to,
        subject: subject,
        html: html
    }
    return new Promise((resolve, reject) => {
        transporter.sendMail(mailOptions, (err) => {
            if (err) return reject(err)
            return resolve()
        })
    })
}

function generateRandomString(length) {
    var result           = '';
    var characters       = '0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < length; i++ ) {
       result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

function generateVerificationCodeEmailHtml(email, code) {
    return `
    <html>
        <head>
        <style>
        body {
            color: black;
            background: white;
            font-family: Arial, Helvetica, sans-serif;
        }
        h1 {
            color: #076f2f;
        }
        .guide {
            font-size: 11px;
            color: gray;
            margin-top: -8px;
        }
        .code {
            font-weight: bold;   
            font-size: 30px;
            color: #076f07;
            margin-top: 12px;
        }
        </style>
        </head>
        <body>
        <h1>농부심 ERP</h1>
        <hr/>
        <p>
            ${email} 계정에 대한 비밀번호를 재설정 하기 위해 아래의 인증코드를 입력 해 주세요.
            <div class="guide">
                인증코드는 보안을 위해 <b>5분 동안만 사용</b>하실 수 있으며, 5분이 지난 후에는 새로운 인증코드를 받으셔야 합니다.
            </div>
            <div class="code">${code}</div>
            <br/>
            감사합니다.
            <br/>
            <i>- 농부심 팀</i>
        </p>
        </body>
    </html>
    `
}