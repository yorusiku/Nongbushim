
module.exports = {
    PORT: 8080,
    STRATEGY: {
        EMAIL: 0,
        KAKAO: 1,
        NAVER: 2
    },
    JWT_SECRET: 'shdqntla#chlrh#gmdgofk',
    COOKIE_SECRET: '&8diwamndd',
    KAKAO_PASSPORT: {
        CLIENT_ID: 'fbf51a0e8e12eef26078575dd6dc4461',
        CLIENT_SECRET: 'EqQTOVZiedXQWnH1rZUgkBRfRU6vFEqy',
        CALLBACK_URL: 'http://localhost:8080/accounts/oauth/kakao'
    },
    DATABASE: {
        host: '127.0.0.1',
        database: 'db_nongpbushim_erp',
        user: 'root',
        password: '1q2w3e',
        connectionLimit: 10,
        supportBigNumbers: true,
        multipleStatements: true,
        charset : 'utf8mb4'
    },
    FARM_USER_STATE: {
        GRANTED: 1,
        PEDING: 0,
        REJECTED: -1
    },
    JOBS: ['소유자', '정직원', '일용직'],
    EMAIL: {
        username: '여기에 이메일 주소',
        password: '여기에 이메일 비밀번호'
    }
}