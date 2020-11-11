
const di = require('./di')
di.inject()

const { Container } = require('typedi')
const ExpressServer = require('./infrastructure/ExpressServer')
const expressServer = new ExpressServer()
const app = expressServer.init()
const server = require('http').createServer(app)

const Constants = Container.get("Constants")
const Logger = Container.get("Logger")

server.listen(Constants.PORT, () => {
    Logger.d("농부심 ERP 서버가 시작되었습니다.")
})