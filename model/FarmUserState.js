const { Container } = require('typedi')
const pool = Container.get('pool')
const ErrorCodes = Container.get('ErrorCodes')
const Jelib = Container.get('Jelib')
const Constants = Container.get('Constants')

const TABLE = 'farm_user_states'

/**
 * 농장에 유저 등록
 * @param fid 농장 ID
 * @param uid 유저 ID
 * @param job 역할
 * @param company 회사
 * @param state 승인상태
 */
exports.registerUser = async (userState) => {
    console.log(userState)
    userState.state = userState.state || 0
    await pool.query(`
        INSERT INTO ${TABLE} SET ? ON DUPLICATE KEY UPDATE state=VALUES(state)
    `, userState)
}

exports.unregisterUser = async (fid, uid) => {
    await pool.query(`
        DELETE FROM ${TABLE} WHERE fid=? AND uid=?
    `, [fid, uid])
}

/**
 * 농장 유저의 승인 상태변경.
 * - 상태를 변경하는 본인이 승인된 유저가 아니라면 권한 없음
 * @param {*} fid 
 * @param {*} uid 
 * @param {*} targetUid 
 * @param {*} state 
 */
exports.updateState = async (fid, uid, targetUid, state) => {
    await pool.query(`
        UPDATE ${TABLE} SET state=? WHERE fid=? AND uid=? LIMIT 1
    `, [state, fid, targetUid])
}

exports.hasPermission = async (fid, uid) => {
    let [[ userState ]] = await pool.query(`
        SELECT * FROM ${TABLE} WHERE fid=? AND uid=? AND state=? LIMIT 1
    `, [fid, uid, Constants.FARM_USER_STATE.GRANTED])
    return userState
}

exports.getStates = async (fid) => {
    let [ userStates ] = await pool.query(`
        SELECT * FROM ${TABLE} WHERE fid=?
    `, fid)
    return userStates
}

exports.update = async (userState) => {
    await pool.query(`
        UPDATE ${TABLE} SET ?
        WHERE fid=? AND uid=? LIMIT 1
    `, [userState, userState.fid, userState.uid])
}